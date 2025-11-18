package com.multi.y2k4.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import com.multi.y2k4.service.finance.UnpaidService;
import com.multi.y2k4.service.transaction.PurchaseDetailsService;
import com.multi.y2k4.service.transaction.PurchaseService;
import com.multi.y2k4.service.transaction.SaleDetailsService;
import com.multi.y2k4.service.transaction.SaleService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
import com.multi.y2k4.vo.finance.Unpaid;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final SaleDetailsService saleDetailsService;
    private final PurchaseDetailsService purchaseDetailsService;
    private final ObjectMapper objectMapper;
    private final DocumentsService documentsService;
    private final ProfitService profitService;
    private final SpendService spendService;
    private final UnpaidService unpaidService;

    @GetMapping("/sale/list")
    public List<Sale> saleList(@RequestParam(required = false) Integer sale_id,
                       @RequestParam(required = false) Integer emp_id,
                       @RequestParam(required = false) Integer ac_id,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                       @RequestParam(required = false) Double total_price,
                       @RequestParam(required = false) Integer status,
                       Model model) {

        //테스트를 위한 수동 리스트 값 생성
        Map<String, Object> body = new HashMap<>();
        body.put("table", "transaction");
        body.put("method", "read");
        body.put("values", saleService.list(sale_id,emp_id,ac_id,order_date,due_date,status));

        try {

            System.out.println( objectMapper.writeValueAsString(body));
        }catch (Exception e){
            e.printStackTrace();
        }
        return saleService.list(sale_id,emp_id,ac_id,order_date,due_date,status);
    }

    @PostMapping("/sale/add")
    @Transactional
    public boolean addSale( @RequestParam(required = false) Integer emp_id,
                            @RequestParam(required = false) Integer ac_id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                            @RequestParam Integer[] stock_id,
                            @RequestParam Integer[] qty,
                            @RequestParam Double[] price_per_unit,
                            Model model) throws JsonProcessingException {

        double total_price = 0;
        List<SaleDetails> saleDetails = new ArrayList<>();

        for(int i = 0; i < stock_id.length; i++){
            SaleDetails saleDetail = new SaleDetails();
            saleDetail.setStock_id(stock_id[i]);
            saleDetail.setQty(qty[i]);
            saleDetail.setPrice_per_unit(price_per_unit[i]);
            saleDetail.setTotal_price(price_per_unit[i]*qty[i]);
            saleDetails.add(saleDetail);
            total_price += saleDetail.getTotal_price();
        }
        Sale sale = new Sale();
        sale.setAc_id(ac_id);   //거래처
        sale.setEmp_id(emp_id); //담당자
        sale.setOrder_date(order_date);
        sale.setDue_date(due_date);
        sale.setTotal_price(total_price);
        /***********************이후론 계정의 등급에 따라 분기*******************************/
        int authLevel = 0;
        Documents doc = new Documents();

        if(authLevel == 0){     //현재 계정의 등급이 문서화를 거칠 필요가 없을떄
            sale.setStatus(0);
            saleService.addSale(sale);
            int id = sale.getSale_id();
            for(int i = 0; i < stock_id.length; i++){
                saleDetails.get(i).setSale_id(id);
            }
            saleDetailsService.addSaleDetails(saleDetails);
            doc.setStatus(1);       //DB의 변경이 일어나므로 기록으로는 남김, 자체 승인을 한 것이므로 문서 상태는 바로 1(승인)
        }else{      //문서화가 필요할 때
            sale.setStatus(99);     //99는 해당 데이터는 비활성화라는 의미, 문서가 승인나면 99 -> 0으로 수정하는 방식
            saleService.addSale(sale);
            int id = sale.getSale_id();
            for(int i = 0; i < stock_id.length; i++){
                saleDetails.get(i).setSale_id(id);
            }
            saleDetailsService.addSaleDetails(saleDetails);

            Unpaid unpaid = new Unpaid();
            unpaid.setCat_id(1);                  // 1 = 판매/구매
            unpaid.setTb_id(0);                   // 0 = 판매
            unpaid.setRef_pk((long) sale.getSale_id());
            unpaid.setCost(total_price);          // 주문 기준 금액
            unpaid.setType(1);                    // 1 = 수익 쪽
            unpaid.setStatus(0);                  // 0 = 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(0);   //처리를 대기중
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 0);    // 판매
        payload.put("cd_id", 0);    // 추가
        payload.put("pk", sale.getSale_id());
        payload.put("sale", sale);
        payload.put("details", saleDetails);
        //제목
        //기안자 emp_id(추후 세션에서 가져옴)
        //결재자 emp_id(추후 세션에서 자신의 emp_id를 통해 supervisor값을 저장),자신이 상위관리자라면 자기 자신의 emp_id
        doc.setReq_date(LocalDate.now());   //결재 올린 시간은 기본적으로 현재 시간
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
            // (3) 문서 저장
        //documentsService.addDocument(doc);


        return true;
    }

    @GetMapping("/sale/viewSaleDetails")
    public List<SaleDetails> saleDetailsList(@RequestParam Integer sale_id){
        System.out.println(sale_id);
        System.out.println(saleDetailsService.searchById(sale_id));
        return saleDetailsService.searchById(sale_id);
    }

    @PostMapping("/sale/editSale")
    @Transactional
    public boolean editSale(@RequestParam Integer sale_id,
                            @RequestParam(required = false) Integer emp_id,
                            @RequestParam(required = false) Integer ac_id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                            @RequestParam Integer[] stock_id,
                            @RequestParam Integer[] qty,
                            @RequestParam Double[] price_per_unit) throws JsonProcessingException {
        Sale before_sale = saleService.searchById(sale_id);    //실질적으로 한개만 검색, 추후 document를 위해 수정 전 값을 검색
        List<SaleDetails> before_saleDetails = saleDetailsService.searchById(sale_id);  //추후 document를 위해 수정 전 값을 검색

        //내부적으로 판매 새부 정보의 수정은 기존의 상세 정보는 모두 삭제, 수정된 내용을 새로 insert
        Sale edit_sale = new Sale();
        edit_sale.setSale_id(sale_id);
        edit_sale.setAc_id(ac_id);
        edit_sale.setEmp_id(emp_id);
        edit_sale.setOrder_date(order_date);
        edit_sale.setDue_date(due_date);

        List<SaleDetails> edit_saleDetail = new ArrayList<>();
        double total_price = 0;
        for(int i = 0; i < stock_id.length; i++){
            SaleDetails saleDetail = new SaleDetails();
            saleDetail.setSale_id(sale_id);
            saleDetail.setStock_id(stock_id[i]);
            saleDetail.setQty(qty[i]);
            saleDetail.setPrice_per_unit(price_per_unit[i]);
            saleDetail.setTotal_price(price_per_unit[i]*qty[i]);
            edit_saleDetail.add(saleDetail);
            total_price += saleDetail.getTotal_price();
        }

        edit_sale.setTotal_price(total_price);

        /***********************이후론 계정의 등급에 따라 분기*******************************/
        int authLevel = 0;
        Documents doc = new Documents();

        if(authLevel == 0){     //현재 계정의 등급이 문서화를 거칠 필요가 없을떄, 기존의 과정 그대로 진행
            saleService.editSale(edit_sale);
            saleDetailsService.deleteSaleDetails(sale_id);
            saleDetailsService.addSaleDetails(edit_saleDetail);

            Unpaid unpaid = unpaidService.searchByBusiness(1, 0, sale_id.longValue());
            unpaid.setCost(edit_sale.getTotal_price());
            unpaid.setStatus(0); // 여전히 미정산
            unpaidService.upsertUnpaid(unpaid);


            doc.setStatus(1);       //DB의 변경이 일어나므로 기록으로는 남김, 자체 승인을 한 것이므로 문서 상태는 바로 1(승인)
        }else{      //문서화가 필요할 때
            before_sale.setStatus(99);
            saleService.editSaleStatus(before_sale);    //아직 수정이 승인나지 않았으므로 기존의 수정 전 정보의 상태를 99로
            doc.setStatus(0);   //처리를 대기중
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("before_sale", before_sale);
        payload.put("before_details", before_saleDetails);
        payload.put("after_sale", edit_sale);
        payload.put("after_details", edit_saleDetail);  //after_**들은 해당 문서가 승인이 되면 기존의 정보들을 대체
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 0);    // 판매
        payload.put("cd_id", 1);    // 수정
        payload.put("before_pk", before_sale.getSale_id());


        //제목
        //기안자 emp_id(추후 세션에서 가져옴)
        //결재자 emp_id(추후 세션에서 자신의 emp_id를 통해 supervisor값을 저장),자신이 상위관리자라면 자기 자신의 emp_id
        doc.setReq_date(LocalDate.now());   //결재 올린 시간은 기본적으로 현재 시간
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        // (3) 문서 저장
        //documentsService.addDocument(doc);

        return true;

    }

    @PostMapping("/sale/editSaleStatus")
    public boolean editSaleStatus(  @RequestParam Integer sale_id,
                                    @RequestParam Integer status) {
        // 0) 현재 판매 정보 조회
        Sale sale = saleService.searchById(sale_id);
        if (sale == null) {
            return false;
        }

        // 1) 판매 상태 변경
        sale.setStatus(status);
        saleService.editSaleStatus(sale);

        // 2) status == 1 일 때만 정산 처리
        if (status == 1) { // 1 = 도착/정산 완료 같은 의미로 사용
            // 2) Unpaid를 정산 완료로만 변경 (금액 수정 X)
            unpaidService.markPaid(1, 0, sale_id.longValue());  // cat=1(판매/구매), tb=0(판매)

            double amount = sale.getTotal_price();   // 금액은 add/edit에서 유지하던 값 사용

            // 3) Profit 기록
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

    @GetMapping("/purchase/list")
    public List<Purchase> purchaseList( @RequestParam(required = false) Integer purchase_id,
                                        @RequestParam(required = false) Integer emp_id,
                                        @RequestParam(required = false) Integer ac_id,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                        @RequestParam(required = false) Integer status,
                                       Model model) {

        //테스트를 위한 수동 리스트 값 생성

        System.out.println(purchaseService.list(purchase_id,emp_id,ac_id,order_date,del_date,status));
        return purchaseService.list(purchase_id,emp_id,ac_id,order_date,del_date,status);
    }

    @PostMapping("/purchase/add")
    @Transactional
    public boolean addPurchase(@RequestParam Integer emp_id,
                                @RequestParam Integer ac_id,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                @RequestParam Integer[] stock_id,
                                @RequestParam Integer[] qty,
                                @RequestParam Double[] price_per_unit,
                           Model model) throws JsonProcessingException {
        double total_price = 0;
        List<PurchaseDetails> purchaseDetails = new ArrayList<>();

        for(int i = 0; i < stock_id.length; i++){
            PurchaseDetails purchaseDetail = new PurchaseDetails();
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setPurchase_qty(qty[i]);
            purchaseDetail.setPrice_per_unit(price_per_unit[i]);
            purchaseDetail.setTotal_price(price_per_unit[i]*qty[i]);
            purchaseDetails.add(purchaseDetail);
            total_price += purchaseDetail.getTotal_price();
        }

        Purchase purchase = new Purchase();
        purchase.setAc_id(ac_id);   //거래처
        purchase.setEmp_id(emp_id); //담당자
        purchase.setOrder_date(order_date);
        purchase.setDel_date(del_date);
        purchase.setTotal_price(total_price);

        /***********************이후론 계정의 등급에 따라 분기*******************************/
        int authLevel = 0;
        Documents doc = new Documents();

        if(authLevel == 0){     //현재 계정의 등급이 문서화를 거칠 필요가 없을떄
            purchase.setStatus(0);
            purchaseService.addPurchase(purchase);
            int id = purchase.getPurchase_id();
            for(int i = 0; i < stock_id.length; i++){
                purchaseDetails.get(i).setPurchase_id(id);
            }
            purchaseDetailsService.addPurchaseDetails(purchaseDetails);

            Unpaid unpaid = new Unpaid();
            unpaid.setCat_id(1);                    // 판매/구매
            unpaid.setTb_id(1);                     // 1 = 구매
            unpaid.setRef_pk((long) purchase.getPurchase_id());
            unpaid.setCost(total_price);
            unpaid.setType(2);                      // 2 = 지출 쪽
            unpaid.setStatus(0);                    // 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(1);       //DB의 변경이 일어나므로 기록으로는 남김, 자체 승인을 한 것이므로 문서 상태는 바로 1(승인)
        }else{      //문서화가 필요할 때
            purchase.setStatus(99);
            purchaseService.addPurchase(purchase);
            int id = purchase.getPurchase_id();
            for(int i = 0; i < stock_id.length; i++){
                purchaseDetails.get(i).setPurchase_id(id);
            }
            purchaseDetailsService.addPurchaseDetails(purchaseDetails);
            doc.setStatus(0);   //처리를 대기중
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 1);    // 구매
        payload.put("cd_id", 0);    // 추가
        payload.put("pk", purchase.getPurchase_id());
        payload.put("purchase", purchase);
        payload.put("details", purchaseDetails);
        //제목
        //기안자 emp_id(추후 세션에서 가져옴)
        //결재자 emp_id(추후 세션에서 자신의 emp_id를 통해 supervisor값을 저장),자신이 상위관리자라면 자기 자신의 emp_id
        doc.setReq_date(LocalDate.now());   //결재 올린 시간은 기본적으로 현재 시간
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        // (3) 문서 저장
        //documentsService.addDocument(doc);

        return true;

    }

    @GetMapping("/purchase/viewPurchaseDetails")
    public List<PurchaseDetails> purchaseDetailsList(@RequestParam Integer purchase_id){
        System.out.println(purchaseDetailsService.searchById(purchase_id));
        return purchaseDetailsService.searchById(purchase_id);
    }

    @PostMapping("/purchase/editPurchase")
    @Transactional
    public boolean editPurchase(@RequestParam Integer purchase_id,
                                @RequestParam Integer emp_id,
                                @RequestParam Integer ac_id,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                @RequestParam Integer[] stock_id,
                                @RequestParam Integer[] qty,
                                @RequestParam Double[] price_per_unit) throws JsonProcessingException {

        Purchase before_purchase = purchaseService.searchById(purchase_id);    //실질적으로 한개만 검색, 추후 document를 위해 수정 전 값을 검색
        List<PurchaseDetails> before_purchaseDetails = purchaseDetailsService.searchById(purchase_id);  //추후 document를 위해 수정 전 값을 검색

        //내부적으로 판매 새부 정보의 수정은 기존의 상세 정보는 모두 삭제, 수정된 내용을 새로 insert
        Purchase edit_purchase = new Purchase();
        edit_purchase.setPurchase_id(purchase_id);
        edit_purchase.setAc_id(ac_id);
        edit_purchase.setEmp_id(emp_id);
        edit_purchase.setOrder_date(order_date);
        edit_purchase.setDel_date(del_date);

        List<PurchaseDetails> edit_purchaseDetail = new ArrayList<>();
        double total_price = 0;
        for(int i = 0; i < stock_id.length; i++){
            PurchaseDetails purchaseDetail = new PurchaseDetails();
            purchaseDetail.setPurchase_id(purchase_id);
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setPurchase_qty(qty[i]);
            purchaseDetail.setPrice_per_unit(price_per_unit[i]);
            purchaseDetail.setTotal_price(price_per_unit[i]*qty[i]);
            edit_purchaseDetail.add(purchaseDetail);
            total_price += purchaseDetail.getTotal_price();
        }

        edit_purchase.setTotal_price(total_price);

        /***********************이후론 계정의 등급에 따라 분기*******************************/
        int authLevel = 0;
        Documents doc = new Documents();

        if(authLevel == 0){     //현재 계정의 등급이 문서화를 거칠 필요가 없을떄, 기존의 과정 그대로 진행
            purchaseService.editPurchase(edit_purchase);
            purchaseDetailsService.deletePurchaseDetails(purchase_id);
            purchaseDetailsService.addPurchaseDetails(edit_purchaseDetail);

            Unpaid unpaid = unpaidService.searchByBusiness(1, 1, purchase_id.longValue());
            unpaid.setCost(edit_purchase.getTotal_price());
            unpaid.setStatus(0); // 여전히 미정산
            unpaidService.upsertUnpaid(unpaid);

            doc.setStatus(1);       //DB의 변경이 일어나므로 기록으로는 남김, 자체 승인을 한 것이므로 문서 상태는 바로 1(승인)
        }else{      //문서화가 필요할 때
            before_purchase.setStatus(99);
            purchaseService.editPurchaseStatus(before_purchase);    //아직 수정이 승인나지 않았으므로 기존의 수정 전 정보의 상태를 99로
            doc.setStatus(0);   //처리를 대기중
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("before_purchase", before_purchase);
        payload.put("before_details", before_purchaseDetails);
        payload.put("after_purchase", edit_purchase);
        payload.put("after_details", edit_purchaseDetail);  //after_**들은 해당 문서가 승인이 되면 기존의 정보들을 대체
        payload.put("cat_id", 1);   // 판매/구매
        payload.put("tb_id", 1);    // 판매
        payload.put("cd_id", 1);    // 수정
        payload.put("before_pk", before_purchase.getPurchase_id());


        //제목
        //기안자 emp_id(추후 세션에서 가져옴)
        //결재자 emp_id(추후 세션에서 자신의 emp_id를 통해 supervisor값을 저장),자신이 상위관리자라면 자기 자신의 emp_id
        doc.setReq_date(LocalDate.now());   //결재 올린 시간은 기본적으로 현재 시간
        String query = objectMapper.writeValueAsString(payload);
        doc.setQuery(query);
        // (3) 문서 저장
        //documentsService.addDocument(doc);

        return true;
    }
    @PostMapping("/purchase/editPurchaseStatus")
    @Transactional
    public boolean editPurchaseStatus(  @RequestParam Integer purchase_id,
                                        @RequestParam Integer[] pd_id,
                                        @RequestParam Integer[] qty) {
        // 0) 상세 정보 가져오기
        List<PurchaseDetails> details = purchaseDetailsService.searchById(purchase_id);
        if (details == null || details.isEmpty()) {
            return false;
        }

        double total_price = 0.0;

        // 1) 도착 수량을 라인별로 반영 (재고/입고 수량용)
        for (int i = 0; i < pd_id.length; i++) {
            Integer currentPdId = pd_id[i];
            Integer arrivedQty  = qty[i];

            PurchaseDetails detail = details.get(i);
            double lineTotal = detail.getPrice_per_unit() * arrivedQty;
            total_price += lineTotal;

            // 도착 수량 업데이트
            purchaseDetailsService.editPurchaseDetailsQTY(currentPdId, arrivedQty);
        }

        // 2) 구매 헤더 total_price 도 실제 도착 기준으로 맞춰주고,
        //    상태(status)도 1(정산 완료/입고 완료)로 변경
        Purchase origin = purchaseService.searchById(purchase_id);
        if (origin == null) return false;

        origin.setTotal_price(total_price);
        purchaseService.editPurchase(origin);  // total_price 반영

        Purchase statusOnly = new Purchase();
        statusOnly.setPurchase_id(purchase_id);
        statusOnly.setStatus(1);
        purchaseService.editPurchaseStatus(statusOnly);

        // 3) Unpaid는 '정산 완료'로만 변경 (금액은 add/edit에서 맞춰두었다고 가정)
        unpaidService.markPaid(1, 1, purchase_id.longValue()); // cat=1, tb=1(구매)

        // 4) 지출 기록
        Spend spend = new Spend();
        spend.setCat_id(1);
        spend.setTb_id(1);
        spend.setSpend(total_price); // 실제 도착 수량 기준
        spend.setSpend_date(LocalDateTime.now());
        spend.setSpend_comment("구매ID " + purchase_id + " 실제 도착 수량 기준 정산");

        spendService.addSpend(spend);

        return true;
    }
}
