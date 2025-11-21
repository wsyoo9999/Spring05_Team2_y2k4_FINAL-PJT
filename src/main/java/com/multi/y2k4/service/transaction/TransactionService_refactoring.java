package com.multi.y2k4.service.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import com.multi.y2k4.service.finance.UnpaidService;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
import com.multi.y2k4.vo.finance.Unpaid;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService_refactoring {

    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final SaleDetailsService saleDetailsService;
    private final PurchaseDetailsService purchaseDetailsService;
    private final DocumentsService documentsService;
    private final ProfitService profitService;
    private final SpendService spendService;
    private final UnpaidService unpaidService;
    private final StockService stockService;
    private final ObjectMapper objectMapper;

    // ==============================================================
    //  공통: 세션에서 결재 컨텍스트 꺼내기
    // ==============================================================

    private static class ApprovalContext {
        final Integer me;
        final Integer supervisor;
        final Integer authLevel;

        private ApprovalContext(Integer me, Integer supervisor, Integer authLevel) {
            this.me = me;
            this.supervisor = supervisor;
            this.authLevel = authLevel;
        }

        boolean isValid() {
            return me != null && supervisor != null && authLevel != null;
        }

        boolean isImmediate() {
            return authLevel != null && authLevel == 0;
        }
    }

    private ApprovalContext getApprovalContext(HttpSession session) {
        Integer me = (Integer) session.getAttribute("me");
        Integer sup = (Integer) session.getAttribute("supervisor");
        Integer authLevel = (Integer) session.getAttribute("authLevel");
        ApprovalContext ctx = new ApprovalContext(me, sup, authLevel);
        return ctx.isValid() ? ctx : null;
    }

    private void fillCommonDocHeader(Documents doc,
                                     ApprovalContext ctx,
                                     int catId,
                                     int tbId,
                                     int cdId) {
        doc.setReq_date(LocalDate.now());
        doc.setReq_id(ctx.me.longValue());
        doc.setAppr_id(ctx.supervisor.longValue());
        doc.setCat_id(catId);
        doc.setTb_id(tbId);
        doc.setCd_id(cdId);
    }

    // ==============================================================
    //  판매 등록
    // ==============================================================

    public boolean addSale(Integer emp_id,
                           Integer ac_id,
                           LocalDate order_date,
                           LocalDate due_date,
                           Integer[] stock_id,
                           Integer[] qty,
                           Double[] price_per_unit,
                           HttpSession httpSession) throws JsonProcessingException {

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        double total_price = 0;
        List<SaleDetails> saleDetails = new ArrayList<>();

        for (int i = 0; i < stock_id.length; i++) {
            SaleDetails saleDetail = new SaleDetails();
            saleDetail.setStock_id(stock_id[i]);
            saleDetail.setQty(qty[i]);
            saleDetail.setPrice_per_unit(price_per_unit[i]);
            saleDetail.setTotal_price(price_per_unit[i] * qty[i]);
            saleDetails.add(saleDetail);
            total_price += saleDetail.getTotal_price();
        }

        Sale sale = new Sale();
        sale.setAc_id(ac_id);
        sale.setEmp_id(emp_id);
        sale.setOrder_date(order_date);
        sale.setDue_date(due_date);
        sale.setTotal_price(total_price);

        Documents doc = new Documents();
        fillCommonDocHeader(doc, ctx, 1, 0, 0);
        doc.setTitle("sale_add 테스트중 " + LocalDate.now());

        Map<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);
        payload.put("tb_id", 0);
        payload.put("cd_id", 0);
        payload.put("sale", sale);
        payload.put("details", saleDetails);

        if (ctx.isImmediate()) {
            applySaleAddImmediate(sale, saleDetails, doc,total_price,payload);
        } else {
            applySaleAddWithApproval(sale, saleDetails, total_price, doc, payload);
        }


        return true;
    }

    // 즉시 반영(권한 있음)
    private void applySaleAddImmediate(Sale sale,
                                       List<SaleDetails> saleDetails,
                                       Documents doc,
                                       double total_price,
                                       Map<String, Object> payload) throws JsonProcessingException {

        sale.setStatus(0);  // 바로 활성
        saleService.addSale(sale);

        int id = sale.getSale_id();
        for (SaleDetails d : saleDetails) {
            d.setSale_id(id);
            // 재고 요청 수량 증가
            stockService.manageAcquiredAty(d.getStock_id(), 1, d.getQty());
        }
        saleDetailsService.addSaleDetails(saleDetails);

        payload.put("pk", sale.getSale_id());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(1);   // 자체 승인
        documentsService.addDocument(doc);

        Unpaid unpaid = new Unpaid();
        unpaid.setCat_id(1);
        unpaid.setTb_id(0);
        unpaid.setRef_pk((long) sale.getSale_id());
        unpaid.setCost(total_price);
        unpaid.setType(1);   // 수익
        unpaid.setStatus(0); // 미정산
        unpaidService.upsertUnpaid(unpaid);
    }

    // 문서 결재 필요(권한 없음)
    private void applySaleAddWithApproval(Sale sale,
                                          List<SaleDetails> saleDetails,
                                          double total_price,
                                          Documents doc,
                                          Map<String, Object> payload) throws JsonProcessingException {

        sale.setStatus(99); // 비활성(승인 대기)
        saleService.addSale(sale);

        int id = sale.getSale_id();
        for (SaleDetails d : saleDetails) {
            d.setSale_id(id);
        }
        saleDetailsService.addSaleDetails(saleDetails);


        payload.put("pk", sale.getSale_id());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(0);    // 처리 대기
        documentsService.addDocument(doc);
    }

    // ==============================================================
    //  판매 수정
    // ==============================================================

    public boolean editSale(Integer sale_id,
                            Integer emp_id,
                            Integer ac_id,
                            LocalDate order_date,
                            LocalDate due_date,
                            Integer[] stock_id,
                            Integer[] qty,
                            Double[] price_per_unit,
                            HttpSession httpSession) throws JsonProcessingException {

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        Sale before_sale = saleService.searchById(sale_id);
        List<SaleDetails> before_saleDetails = saleDetailsService.searchById(sale_id);

        List<Integer> before_stocks = new ArrayList<>();
        List<Integer> before_qty = new ArrayList<>();
        for (SaleDetails sd : before_saleDetails) {
            before_stocks.add(sd.getStock_id());
            before_qty.add(sd.getQty());
        }

        Sale edit_sale = new Sale();
        edit_sale.setSale_id(sale_id);
        edit_sale.setAc_id(ac_id);
        edit_sale.setEmp_id(emp_id);
        edit_sale.setOrder_date(order_date);
        edit_sale.setDue_date(due_date);

        List<SaleDetails> edit_saleDetail = new ArrayList<>();
        List<Integer> after_stocks = new ArrayList<>();
        List<Integer> after_qty = new ArrayList<>();
        double total_price = 0;
        for (int i = 0; i < stock_id.length; i++) {
            SaleDetails sd = new SaleDetails();
            sd.setSale_id(sale_id);
            sd.setStock_id(stock_id[i]);
            sd.setQty(qty[i]);
            sd.setPrice_per_unit(price_per_unit[i]);
            sd.setTotal_price(price_per_unit[i] * qty[i]);
            edit_saleDetail.add(sd);

            after_stocks.add(sd.getStock_id());
            after_qty.add(sd.getQty());
            total_price += sd.getTotal_price();
        }
        edit_sale.setTotal_price(total_price);

        Documents doc = new Documents();
        fillCommonDocHeader(doc, ctx, 1, 0, 1); // cat=1, tb=0(판매), cd=1(수정)

        Map<String, Object> payload = new HashMap<>();
        payload.put("before_sale", before_sale);
        payload.put("before_details", before_saleDetails);
        payload.put("after_sale", edit_sale);
        payload.put("after_details", edit_saleDetail);
        payload.put("cat_id", 1);
        payload.put("tb_id", 0);
        payload.put("cd_id", 1);
        payload.put("before_pk", before_sale.getSale_id());

        if (ctx.isImmediate()) {
            applySaleEditImmediate(before_stocks, before_qty, after_stocks, after_qty,
                    edit_sale, edit_saleDetail, doc, payload);
        } else {
            applySaleEditWithApproval(before_sale, doc, payload);
        }

        return true;
    }

    // 판매 수정 즉시 반영
    private void applySaleEditImmediate(List<Integer> before_stocks,
                                        List<Integer> before_qty,
                                        List<Integer> after_stocks,
                                        List<Integer> after_qty,
                                        Sale edit_sale,
                                        List<SaleDetails> edit_saleDetail,
                                        Documents doc,
                                        Map<String, Object> payload) throws JsonProcessingException {

        // 이전 요청 수량 원복
        for (int i = 0; i < before_stocks.size(); i++) {
            stockService.manageAcquiredAty(before_stocks.get(i), 2, before_qty.get(i));
        }

        // 새 요청 수량 반영
        for (int i = 0; i < after_stocks.size(); i++) {
            stockService.manageAcquiredAty(after_stocks.get(i), 1, after_qty.get(i));
        }

        saleService.editSale(edit_sale);
        saleDetailsService.deleteSaleDetails(edit_sale.getSale_id());
        saleDetailsService.addSaleDetails(edit_saleDetail);

        Unpaid unpaid = unpaidService.searchByBusiness(1, 0, (long) edit_sale.getSale_id());
        unpaid.setCost(edit_sale.getTotal_price());
        unpaid.setStatus(0);
        unpaidService.upsertUnpaid(unpaid);

        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(1); // 자체 승인
        documentsService.addDocument(doc);
    }

    // 판매 수정 결재 필요
    private void applySaleEditWithApproval(Sale before_sale,
                                           Documents doc,
                                           Map<String, Object> payload) throws JsonProcessingException {

        // 기존 데이터를 잠깐 잠금 상태로 유지(정책에 따라 status 값 조절)
        before_sale.setStatus(0);
        saleService.editSaleStatus(before_sale);

        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(0); // 처리 대기
        documentsService.addDocument(doc);
    }

    // ==============================================================
    //  판매 상태 변경 (출고/도착)
    // ==============================================================

    public boolean editSaleStatus(Integer sale_id, Integer status, HttpSession httpSession) {
        Sale sale = saleService.searchById(sale_id);
        if (sale == null) return false;

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        List<SaleDetails> saleDetails = saleDetailsService.searchById(sale_id);
        List<Integer> stock_id = new ArrayList<>();
        List<Integer> qty = new ArrayList<>();
        for (SaleDetails sd : saleDetails) {
            stock_id.add(sd.getStock_id());
            qty.add(sd.getQty());
        }

        sale.setStatus(status);

        if (status == 1) {    // 배송 시작 -> 재고 체크
            List<Integer> result = stockService.manageStock(stock_id, qty, qty, 2);
            if (result != null) {
                saleService.editSaleStatus(sale);
            } else {
                return false;
            }
        } else if (status == 2) { // 도착 -> 수익 정산
            saleService.editSaleStatus(sale);

            unpaidService.markPaid(1, 0, sale_id.longValue());

            double amount = sale.getTotal_price();
            Profit profit = new Profit();
            profit.setCat_id(1);
            profit.setTb_id(0);
            profit.setProfit(amount);
            profit.setProfit_date(LocalDateTime.now());
            profit.setProfit_comment("판매ID " + sale_id + " 도착/정산");
            profitService.addProfit(profit);
        }

        return true;
    }

    // ==============================================================
    //  구매 등록
    // ==============================================================

    public boolean addPurchase(Integer emp_id,
                               Integer ac_id,
                               LocalDate order_date,
                               LocalDate del_date,
                               Integer[] stock_id,
                               Integer[] qty,
                               Double[] price_per_unit,
                               HttpSession httpSession) throws JsonProcessingException {

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        double total_price = 0;
        List<PurchaseDetails> purchaseDetails = new ArrayList<>();

        for (int i = 0; i < stock_id.length; i++) {
            PurchaseDetails pd = new PurchaseDetails();
            pd.setStock_id(stock_id[i]);
            pd.setPurchase_qty(qty[i]);
            pd.setPrice_per_unit(price_per_unit[i]);
            pd.setTotal_price(price_per_unit[i] * qty[i]);
            purchaseDetails.add(pd);
            total_price += pd.getTotal_price();
        }

        Purchase purchase = new Purchase();
        purchase.setAc_id(ac_id);
        purchase.setEmp_id(emp_id);
        purchase.setOrder_date(order_date);
        purchase.setDel_date(del_date);
        purchase.setTotal_price(total_price);

        Documents doc = new Documents();
        fillCommonDocHeader(doc, ctx, 1, 1, 0); // cat=1, tb=1(구매), cd=0(추가)

        Map<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);
        payload.put("tb_id", 1);
        payload.put("cd_id", 0);
        payload.put("purchase", purchase);
        payload.put("details", purchaseDetails);

        if (ctx.isImmediate()) {
            applyPurchaseAddImmediate(purchase, purchaseDetails, total_price, doc, payload);
        } else {
            applyPurchaseAddWithApproval(purchase, purchaseDetails, doc, payload);
        }

        return true;
    }

    // 구매 추가 즉시 반영 (원래 authLevel==0 분기)
    private void applyPurchaseAddImmediate(Purchase purchase,
                                           List<PurchaseDetails> purchaseDetails,
                                           double total_price,
                                           Documents doc,
                                           Map<String, Object> payload) throws JsonProcessingException {

        purchase.setStatus(0);
        purchaseService.addPurchase(purchase);
        int id = purchase.getPurchase_id();
        for (PurchaseDetails d : purchaseDetails) {
            d.setPurchase_id(id);
        }
        purchaseDetailsService.addPurchaseDetails(purchaseDetails);

        // 미지급금 생성
        Unpaid unpaid = new Unpaid();
        unpaid.setCat_id(1);
        unpaid.setTb_id(1);                  // 1 = 구매
        unpaid.setRef_pk((long) purchase.getPurchase_id());
        unpaid.setCost(total_price);
        unpaid.setType(2);                   // 2 = 지출
        unpaid.setStatus(0);                 // 미정산
        unpaidService.upsertUnpaid(unpaid);

        payload.put("pk", purchase.getPurchase_id());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(1); // 자체 승인
        documentsService.addDocument(doc);
    }

    // 구매 추가 결재 필요 (원래 authLevel!=0 분기)
    private void applyPurchaseAddWithApproval(Purchase purchase,
                                              List<PurchaseDetails> purchaseDetails,
                                              Documents doc,
                                              Map<String, Object> payload) throws JsonProcessingException {

        purchase.setStatus(99);
        purchaseService.addPurchase(purchase);
        int id = purchase.getPurchase_id();
        for (PurchaseDetails d : purchaseDetails) {
            d.setPurchase_id(id);
        }
        purchaseDetailsService.addPurchaseDetails(purchaseDetails);

        // 🔥 원래 코드에서는 이 분기에서는 미지급금은 만들지 않음
        payload.put("pk", purchase.getPurchase_id());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(0);
        documentsService.addDocument(doc);
    }

    // ==============================================================
    //  구매 수정
    // ==============================================================

    public boolean editPurchase(Integer purchase_id,
                                Integer emp_id,
                                Integer ac_id,
                                LocalDate order_date,
                                LocalDate del_date,
                                Integer[] stock_id,
                                Integer[] qty,
                                Double[] price_per_unit,
                                HttpSession httpSession) throws JsonProcessingException {

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        Purchase before_purchase = purchaseService.searchById(purchase_id);
        List<PurchaseDetails> before_purchaseDetails = purchaseDetailsService.searchById(purchase_id);

        Purchase edit_purchase = new Purchase();
        edit_purchase.setPurchase_id(purchase_id);
        edit_purchase.setAc_id(ac_id);
        edit_purchase.setEmp_id(emp_id);
        edit_purchase.setOrder_date(order_date);
        edit_purchase.setDel_date(del_date);

        List<PurchaseDetails> edit_purchaseDetail = new ArrayList<>();
        double total_price = 0;
        for (int i = 0; i < stock_id.length; i++) {
            PurchaseDetails pd = new PurchaseDetails();
            pd.setPurchase_id(purchase_id);
            pd.setStock_id(stock_id[i]);
            pd.setPurchase_qty(qty[i]);
            pd.setPrice_per_unit(price_per_unit[i]);
            pd.setTotal_price(price_per_unit[i] * qty[i]);
            edit_purchaseDetail.add(pd);
            total_price += pd.getTotal_price();
        }
        edit_purchase.setTotal_price(total_price);

        Documents doc = new Documents();
        fillCommonDocHeader(doc, ctx, 1, 1, 1); // cat=1, tb=1(구매), cd=1(수정)
        doc.setTitle("구매 수정 테스트 " + LocalDate.now());

        Map<String, Object> payload = new HashMap<>();
        payload.put("before_purchase", before_purchase);
        payload.put("before_details", before_purchaseDetails);
        payload.put("after_purchase", edit_purchase);
        payload.put("after_details", edit_purchaseDetail);
        payload.put("cat_id", 1);
        payload.put("tb_id", 1);
        payload.put("cd_id", 1);
        payload.put("before_pk", before_purchase.getPurchase_id());

        if (ctx.isImmediate()) {
            applyPurchaseEditImmediate(edit_purchase, edit_purchaseDetail, doc, payload);
        } else {
            applyPurchaseEditWithApproval(before_purchase, doc, payload);
        }

        return true;
    }

    private void applyPurchaseEditImmediate(Purchase edit_purchase,
                                            List<PurchaseDetails> edit_purchaseDetail,
                                            Documents doc,
                                            Map<String, Object> payload) throws JsonProcessingException {

        purchaseService.editPurchase(edit_purchase);
        purchaseDetailsService.deletePurchaseDetails(edit_purchase.getPurchase_id());
        purchaseDetailsService.addPurchaseDetails(edit_purchaseDetail);

        Unpaid unpaid = unpaidService.searchByBusiness(1, 1, (long) edit_purchase.getPurchase_id());
        unpaid.setCost(edit_purchase.getTotal_price());
        unpaid.setStatus(0);
        unpaidService.upsertUnpaid(unpaid);

        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(1);
        documentsService.addDocument(doc);
    }

    private void applyPurchaseEditWithApproval(Purchase before_purchase,
                                               Documents doc,
                                               Map<String, Object> payload) throws JsonProcessingException {

        before_purchase.setStatus(0);
        purchaseService.editPurchaseStatus(before_purchase);

        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setStatus(0);
        documentsService.addDocument(doc);
    }

    // ==============================================================
    //  구매 상태 변경 (실제 입고/정산)
    // ==============================================================

    public boolean editPurchaseStatus(Integer purchase_id,
                                      Integer[] pd_id,
                                      Integer[] qty,
                                      HttpSession httpSession) {

        ApprovalContext ctx = getApprovalContext(httpSession);
        if (ctx == null) return false;

        List<PurchaseDetails> details = purchaseDetailsService.searchById(purchase_id);
        if (details == null || details.isEmpty()) return false;

        double total_price = 0.0;
        List<Integer> arr_qty = new ArrayList<>();
        List<Integer> stocks = new ArrayList<>();

        for (int i = 0; i < pd_id.length; i++) {
            Integer currentPdId = pd_id[i];
            Integer arrivedQty  = qty[i];

            PurchaseDetails detail = details.get(i);
            double lineTotal = detail.getPrice_per_unit() * arrivedQty;
            total_price += lineTotal;

            stocks.add(detail.getStock_id());
            arr_qty.add(arrivedQty);

            purchaseDetailsService.editPurchaseDetailsQTY(currentPdId, arrivedQty);
        }

        stockService.manageStock(stocks, arr_qty, null, 1);

        Purchase origin = purchaseService.searchById(purchase_id);
        if (origin == null) return false;

        origin.setTotal_price(total_price);
        purchaseService.editPurchase(origin);

        Purchase statusOnly = new Purchase();
        statusOnly.setPurchase_id(purchase_id);
        statusOnly.setStatus(1);
        purchaseService.editPurchaseStatus(statusOnly);

        unpaidService.markPaid(1, 1, purchase_id.longValue());

        Spend spend = new Spend();
        spend.setCat_id(1);
        spend.setTb_id(1);
        spend.setSpend(total_price);
        spend.setSpend_date(LocalDateTime.now());
        spend.setSpend_comment("구매ID " + purchase_id + " 실제 도착 수량 기준 정산");

        spendService.addSpend(spend);

        return true;
    }

    // ==============================================================
    //  전자결재 승인/반려 처리 (cat_id=1: 판매/구매)
    // ==============================================================

    public void processSalePurchaseApproval(int catId,
                                            int tbId,
                                            int cdId,
                                            int status,
                                            Map<String, Object> map) {

        if (catId != 1) return;

        if (tbId == 0) {
            handleSaleApproval(cdId, status, map);
        } else if (tbId == 1) {
            handlePurchaseApproval(cdId, status, map);
        }
    }

    // 판매 결재 처리
    private void handleSaleApproval(int cdId,
                                    int status,
                                    Map<String, Object> map) {

        final int catId = 1;
        final int tbId = 0;
        Unpaid unpaid = new Unpaid();

        // 추가
        if (cdId == 0) {
            int pk = Integer.parseInt(map.get("pk").toString());

            if (status == 1) { // 승인
                Sale s = new Sale();
                s.setSale_id(pk);
                s.setStatus(0);
                saleService.editSaleStatus(s);

                Sale sale = saleService.searchById(pk);
                List<SaleDetails> saleDetails = saleDetailsService.searchById(pk);

                for (SaleDetails d : saleDetails) {
                    stockService.manageAcquiredAty(d.getStock_id(), 1, d.getQty());
                }

                unpaid.setCat_id(catId);
                unpaid.setTb_id(tbId);
                unpaid.setRef_pk((long) pk);
                unpaid.setCost(sale.getTotal_price());
                unpaid.setStatus(0);
                unpaid.setType(1);
                unpaidService.upsertUnpaid(unpaid);

            } else if (status == 2) { // 반려
                saleService.deleteSale(pk);
                //unpaidService.cancelUnpaid(catId, tbId, (long) pk);
            }
        }

        // 수정
        else if (cdId == 1) {
            int beforePk = Integer.parseInt(map.get("before_pk").toString());

            if (status == 1) { // 승인
                Object saleObj = map.get("after_sale");
                Object detailsObj = map.get("after_details");

                Sale sale = (saleObj != null)
                        ? objectMapper.convertValue(saleObj, Sale.class)
                        : null;

                List<SaleDetails> afterDetails = (detailsObj != null)
                        ? objectMapper.convertValue(detailsObj, new TypeReference<List<SaleDetails>>() {})
                        : Collections.emptyList();

                List<SaleDetails> beforeDetails = saleDetailsService.searchById(beforePk);

                // 기존 요청 수량 원복
                for (SaleDetails beforeDetail : beforeDetails) {
                    stockService.manageAcquiredAty(beforeDetail.getStock_id(), 2, beforeDetail.getQty());
                }

                // 새 요청 수량 반영
                for (SaleDetails d : afterDetails) {
                    stockService.manageAcquiredAty(d.getStock_id(), 1, d.getQty());
                }

                saleDetailsService.deleteSaleDetails(beforePk);
                saleDetailsService.addSaleDetails(afterDetails);

                sale.setStatus(0);
                saleService.editSale(sale);

                unpaid.setCat_id(catId);
                unpaid.setTb_id(tbId);
                unpaid.setRef_pk((long) beforePk);
                unpaid.setCost(sale.getTotal_price());
                unpaid.setStatus(0);
                unpaid.setType(1);
                unpaidService.upsertUnpaid(unpaid);

            } else if (status == 2) { // 반려 → 기존 데이터 유지
                Sale s = new Sale();
                s.setSale_id(beforePk);
                s.setStatus(0);
                saleService.editSaleStatus(s);
            }
        }

        // 삭제
        else if (cdId == 2) {
            int pk = Integer.parseInt(map.get("pk").toString());

            if (status == 1) { // 삭제 승인
                saleService.deleteSale(pk);
                unpaidService.cancelUnpaid(catId, tbId, (long) pk);
            } else if (status == 2) { // 삭제 반려 → 기존 데이터 유지
                Sale s = new Sale();
                s.setSale_id(pk);
                s.setStatus(0);
                saleService.editSaleStatus(s);
            }
        }
    }

    // 구매 결재 처리
    private void handlePurchaseApproval(int cdId,
                                        int status,
                                        Map<String, Object> map) {

        final int catId = 1;
        final int tbId = 1;
        Unpaid unpaid = new Unpaid();

        // 추가
        if (cdId == 0) {
            int pk = Integer.parseInt(map.get("pk").toString());

            if (status == 1) { // 승인
                Purchase p = new Purchase();
                p.setPurchase_id(pk);
                p.setStatus(0);
                purchaseService.editPurchaseStatus(p);

                Purchase purchase = purchaseService.searchById(pk);

                unpaid.setCat_id(catId);
                unpaid.setTb_id(tbId);
                unpaid.setRef_pk((long) pk);
                unpaid.setCost(purchase.getTotal_price());
                unpaid.setStatus(0);
                unpaid.setType(2);
                unpaidService.upsertUnpaid(unpaid);

            } else if (status == 2) { // 반려
                purchaseService.deletePurchase(pk);
                //unpaidService.cancelUnpaid(catId, tbId, (long) pk);
            }
        }

        // 수정
        else if (cdId == 1) {
            int beforePk = Integer.parseInt(map.get("before_pk").toString());

            if (status == 1) { // 승인
                Object purchaseObj = map.get("after_purchase");
                Object detailsObj = map.get("after_details");

                Purchase purchase = (purchaseObj != null)
                        ? objectMapper.convertValue(purchaseObj, Purchase.class)
                        : null;

                List<PurchaseDetails> detailsList = (detailsObj != null)
                        ? objectMapper.convertValue(detailsObj, new TypeReference<List<PurchaseDetails>>() {})
                        : Collections.emptyList();

                purchaseDetailsService.deletePurchaseDetails(beforePk);
                purchaseDetailsService.addPurchaseDetails(detailsList);

                purchase.setStatus(0);
                purchaseService.editPurchase(purchase);

                unpaid.setCat_id(catId);
                unpaid.setTb_id(tbId);
                unpaid.setRef_pk((long) beforePk);
                unpaid.setCost(purchase.getTotal_price());
                unpaid.setStatus(0);
                unpaid.setType(2);
                unpaidService.upsertUnpaid(unpaid);

            } else if (status == 2) { // 반려
                Purchase p = new Purchase();
                p.setPurchase_id(beforePk);
                p.setStatus(0);
                purchaseService.editPurchaseStatus(p);
            }
        }

        // 삭제
        else if (cdId == 2) {
            int pk = Integer.parseInt(map.get("pk").toString());

            if (status == 1) { // 삭제 승인
                purchaseService.deletePurchase(pk);
                unpaidService.cancelUnpaid(catId, tbId, (long) pk);
            } else if (status == 2) { // 삭제 반려
                Purchase p = new Purchase();
                p.setPurchase_id(pk);
                p.setStatus(0);
                purchaseService.editPurchaseStatus(p);
            }
        }
    }
}
