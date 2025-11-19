package com.multi.y2k4.service.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

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

    // ======================== 판매 등록 ========================
    @Transactional
    public boolean addSale(Integer emp_id,
                           Integer ac_id,
                           LocalDate order_date,
                           LocalDate due_date,
                           Integer[] stock_id,
                           Integer[] qty,
                           Double[] price_per_unit,
                           HttpSession httpSession) throws JsonProcessingException {

        double total_price = 0;
        List<SaleDetails> saleDetails = new ArrayList<>();

        Integer me  = (Integer) httpSession.getAttribute("me");
        Integer sup = (Integer) httpSession.getAttribute("supervisor");
        Integer authLevel = (Integer) httpSession.getAttribute("authLevel");

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
        sale.setAc_id(ac_id);   // 거래처
        sale.setEmp_id(emp_id); // 담당자
        sale.setOrder_date(order_date);
        sale.setDue_date(due_date);
        sale.setTotal_price(total_price);

        Documents doc = new Documents();

        if (authLevel == 0) { // 문서화 불필요(바로 확정)
            sale.setStatus(0);
            saleService.addSale(sale);

            int id = sale.getSale_id();
            for (SaleDetails d : saleDetails) {
                d.setSale_id(id);
            }
            saleDetailsService.addSaleDetails(saleDetails);

            // 자체 승인
            doc.setStatus(1);
        } else {               // 문서화 필요
            sale.setStatus(99); // 비활성(승인 대기)
            saleService.addSale(sale);

            int id = sale.getSale_id();
            for (SaleDetails d : saleDetails) {
                d.setSale_id(id);
            }
            saleDetailsService.addSaleDetails(saleDetails);

            Unpaid unpaid = new Unpaid();
            unpaid.setCat_id(1);                  // 1 = 판매/구매
            unpaid.setTb_id(0);                   // 0 = 판매
            unpaid.setRef_pk((long) sale.getSale_id());
            unpaid.setCost(total_price);          // 주문 기준 금액
            unpaid.setType(1);                    // 1 = 수익
            unpaid.setStatus(0);                  // 0 = 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(0);   // 처리 대기
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 0);    // 판매
        payload.put("cd_id", 0);    // 추가
        payload.put("pk", sale.getSale_id());
        payload.put("sale", sale);
        payload.put("details", saleDetails);

        doc.setReq_date(LocalDate.now());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);

        doc.setReq_id(me.longValue());
        doc.setAppr_id(sup.longValue());
        doc.setTitle("sale_add 테스트중 " + LocalDate.now());

        documentsService.addDocument(doc);

        return true;
    }

    // ======================== 판매 수정 ========================
    @Transactional
    public boolean editSale(Integer sale_id,
                            Integer emp_id,
                            Integer ac_id,
                            LocalDate order_date,
                            LocalDate due_date,
                            Integer[] stock_id,
                            Integer[] qty,
                            Double[] price_per_unit,
                            HttpSession httpSession) throws JsonProcessingException {

        Integer me  = (Integer) httpSession.getAttribute("me");
        Integer sup = (Integer) httpSession.getAttribute("supervisor");
        Integer authLevel = (Integer) httpSession.getAttribute("authLevel");

        Sale before_sale = saleService.searchById(sale_id);
        List<SaleDetails> before_saleDetails = saleDetailsService.searchById(sale_id);

        Sale edit_sale = new Sale();
        edit_sale.setSale_id(sale_id);
        edit_sale.setAc_id(ac_id);
        edit_sale.setEmp_id(emp_id);
        edit_sale.setOrder_date(order_date);
        edit_sale.setDue_date(due_date);

        List<SaleDetails> edit_saleDetail = new ArrayList<>();
        double total_price = 0;
        for (int i = 0; i < stock_id.length; i++) {
            SaleDetails saleDetail = new SaleDetails();
            saleDetail.setSale_id(sale_id);
            saleDetail.setStock_id(stock_id[i]);
            saleDetail.setQty(qty[i]);
            saleDetail.setPrice_per_unit(price_per_unit[i]);
            saleDetail.setTotal_price(price_per_unit[i] * qty[i]);
            edit_saleDetail.add(saleDetail);
            total_price += saleDetail.getTotal_price();
        }
        edit_sale.setTotal_price(total_price);

        Documents doc = new Documents();

        if (authLevel == 0) {   // 바로 수정 반영
            saleService.editSale(edit_sale);
            saleDetailsService.deleteSaleDetails(sale_id);
            saleDetailsService.addSaleDetails(edit_saleDetail);

            Unpaid unpaid = unpaidService.searchByBusiness(1, 0, sale_id.longValue());
            unpaid.setCost(edit_sale.getTotal_price());
            unpaid.setStatus(0); // 여전히 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(1);   // 자체 승인
        } else {               // 문서화 필요
            before_sale.setStatus(0);
            saleService.editSaleStatus(before_sale);    // 기존 데이터를 99로
            doc.setStatus(0);   // 처리 대기
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("before_sale", before_sale);
        payload.put("before_details", before_saleDetails);
        payload.put("after_sale", edit_sale);
        payload.put("after_details", edit_saleDetail);
        payload.put("cat_id", 1);
        payload.put("tb_id", 0);
        payload.put("cd_id", 1); // 수정
        payload.put("before_pk", before_sale.getSale_id());

        doc.setReq_date(LocalDate.now());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setReq_id(Long.valueOf(me));
        doc.setAppr_id(Long.valueOf(sup));
        // 제목은 기존 주석처럼 이후에 채워도 됨

        documentsService.addDocument(doc);
        return true;
    }

    // ======================== 판매 상태 변경 ========================
    @Transactional
    public boolean editSaleStatus(Integer sale_id, Integer status) {
        Sale sale = saleService.searchById(sale_id);
        if (sale == null) {
            return false;
        }
        List<SaleDetails> saleDetails = saleDetailsService.searchById(sale_id);
        List<Integer> stock_id = new ArrayList<>();
        List<Integer> qty = new ArrayList<>();
        for (SaleDetails saleDetail : saleDetails) {
            stock_id.add(saleDetail.getStock_id());
            qty.add(saleDetail.getQty());
        }

        sale.setStatus(status);

        List<Integer> result;
        if (status == 1) {    // 배송 시작 -> 재고 체크
            result = stockService.manageStock(stock_id, qty, 3); // null이 아니면 성공
            if (result != null) {
                saleService.editSaleStatus(sale);
            } else {
                return false;
            }
        } else if (status == 2) { // 도착 -> 수익 정산
            saleService.editSaleStatus(sale);

            unpaidService.markPaid(1, 0, sale_id.longValue());  // cat=1(판매/구매), tb=0(판매)

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

    // ======================== 구매 등록 ========================
    @Transactional
    public boolean addPurchase(Integer emp_id,
                               Integer ac_id,
                               LocalDate order_date,
                               LocalDate del_date,
                               Integer[] stock_id,
                               Integer[] qty,
                               Double[] price_per_unit,
                               HttpSession httpSession) throws JsonProcessingException {

        double total_price = 0;
        List<PurchaseDetails> purchaseDetails = new ArrayList<>();

        Integer me  = (Integer) httpSession.getAttribute("me");
        Integer sup = (Integer) httpSession.getAttribute("supervisor");
        Integer authLevel = (Integer) httpSession.getAttribute("authLevel");

        for (int i = 0; i < stock_id.length; i++) {
            PurchaseDetails purchaseDetail = new PurchaseDetails();
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setPurchase_qty(qty[i]);
            purchaseDetail.setPrice_per_unit(price_per_unit[i]);
            purchaseDetail.setTotal_price(price_per_unit[i] * qty[i]);
            purchaseDetails.add(purchaseDetail);
            total_price += purchaseDetail.getTotal_price();
        }

        Purchase purchase = new Purchase();
        purchase.setAc_id(ac_id);
        purchase.setEmp_id(emp_id);
        purchase.setOrder_date(order_date);
        purchase.setDel_date(del_date);
        purchase.setTotal_price(total_price);

        Documents doc = new Documents();

        if (authLevel == 0) { // 바로 반영
            purchase.setStatus(0);
            purchaseService.addPurchase(purchase);
            int id = purchase.getPurchase_id();
            for (PurchaseDetails d : purchaseDetails) {
                d.setPurchase_id(id);
            }
            purchaseDetailsService.addPurchaseDetails(purchaseDetails);

            Unpaid unpaid = new Unpaid();
            unpaid.setCat_id(1);                 // 판매/구매
            unpaid.setTb_id(1);                  // 1 = 구매
            unpaid.setRef_pk((long) purchase.getPurchase_id());
            unpaid.setCost(total_price);
            unpaid.setType(2);                   // 2 = 지출
            unpaid.setStatus(0);                 // 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(1);
        } else {           // 문서화 필요
            purchase.setStatus(99);
            purchaseService.addPurchase(purchase);
            int id = purchase.getPurchase_id();
            for (PurchaseDetails d : purchaseDetails) {
                d.setPurchase_id(id);
            }
            purchaseDetailsService.addPurchaseDetails(purchaseDetails);
            doc.setStatus(0);
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 1);    // 구매
        payload.put("cd_id", 0);    // 추가
        payload.put("pk", purchase.getPurchase_id());
        payload.put("purchase", purchase);
        payload.put("details", purchaseDetails);

        doc.setReq_date(LocalDate.now());
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setReq_id(Long.valueOf(me));
        doc.setAppr_id(Long.valueOf(sup));

        documentsService.addDocument(doc);
        return true;
    }

    // ======================== 구매 수정 ========================
    @Transactional
    public boolean editPurchase(Integer purchase_id,
                                Integer emp_id,
                                Integer ac_id,
                                LocalDate order_date,
                                LocalDate del_date,
                                Integer[] stock_id,
                                Integer[] qty,
                                Double[] price_per_unit,
                                HttpSession httpSession) throws JsonProcessingException {

        Purchase before_purchase = purchaseService.searchById(purchase_id);
        List<PurchaseDetails> before_purchaseDetails = purchaseDetailsService.searchById(purchase_id);

        Integer me  = (Integer) httpSession.getAttribute("me");
        Integer sup = (Integer) httpSession.getAttribute("supervisor");
        Integer authLevel = (Integer) httpSession.getAttribute("authLevel");

        Purchase edit_purchase = new Purchase();
        edit_purchase.setPurchase_id(purchase_id);
        edit_purchase.setAc_id(ac_id);
        edit_purchase.setEmp_id(emp_id);
        edit_purchase.setOrder_date(order_date);
        edit_purchase.setDel_date(del_date);

        List<PurchaseDetails> edit_purchaseDetail = new ArrayList<>();
        double total_price = 0;
        for (int i = 0; i < stock_id.length; i++) {
            PurchaseDetails purchaseDetail = new PurchaseDetails();
            purchaseDetail.setPurchase_id(purchase_id);
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setPurchase_qty(qty[i]);
            purchaseDetail.setPrice_per_unit(price_per_unit[i]);
            purchaseDetail.setTotal_price(price_per_unit[i] * qty[i]);
            edit_purchaseDetail.add(purchaseDetail);
            total_price += purchaseDetail.getTotal_price();
        }
        edit_purchase.setTotal_price(total_price);

        Documents doc = new Documents();

        if (authLevel == 0) {
            purchaseService.editPurchase(edit_purchase);
            purchaseDetailsService.deletePurchaseDetails(purchase_id);
            purchaseDetailsService.addPurchaseDetails(edit_purchaseDetail);

            Unpaid unpaid = unpaidService.searchByBusiness(1, 1, purchase_id.longValue());
            unpaid.setCost(edit_purchase.getTotal_price());
            unpaid.setStatus(0);
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(1);
        } else {
            before_purchase.setStatus(0);
            purchaseService.editPurchaseStatus(before_purchase);
            doc.setStatus(0);
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("before_purchase", before_purchase);
        payload.put("before_details", before_purchaseDetails);
        payload.put("after_purchase", edit_purchase);
        payload.put("after_details", edit_purchaseDetail);
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 1);    // 판매 (원래 코드 그대로)
        payload.put("cd_id", 1);    // 수정
        payload.put("before_pk", before_purchase.getPurchase_id());

        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        doc.setReq_id(Long.valueOf(me));
        doc.setAppr_id(Long.valueOf(sup));
        doc.setTitle("구매 수정 테스트 " + LocalDate.now());

        documentsService.addDocument(doc);
        return true;
    }

    // ======================== 구매 상태 변경 ========================
    @Transactional
    public boolean editPurchaseStatus(Integer purchase_id,
                                      Integer[] pd_id,
                                      Integer[] qty) {

        List<PurchaseDetails> details = purchaseDetailsService.searchById(purchase_id);
        if (details == null || details.isEmpty()) {
            return false;
        }

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

        // 기존 코드 그대로: 결과값은 사용하지 않음
        stockService.manageStock(stocks, arr_qty, 1);

        Purchase origin = purchaseService.searchById(purchase_id);
        if (origin == null) return false;

        origin.setTotal_price(total_price);
        purchaseService.editPurchase(origin);

        Purchase statusOnly = new Purchase();
        statusOnly.setPurchase_id(purchase_id);
        statusOnly.setStatus(1);
        purchaseService.editPurchaseStatus(statusOnly);

        unpaidService.markPaid(1, 1, purchase_id.longValue()); // cat=1, tb=1(구매)

        Spend spend = new Spend();
        spend.setCat_id(1);
        spend.setTb_id(1);
        spend.setSpend(total_price);
        spend.setSpend_date(LocalDateTime.now());
        spend.setSpend_comment("구매ID " + purchase_id + " 실제 도착 수량 기준 정산");

        spendService.addSpend(spend);

        return true;
    }
}
