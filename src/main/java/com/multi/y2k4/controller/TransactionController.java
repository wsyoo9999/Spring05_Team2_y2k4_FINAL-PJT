package com.multi.y2k4.controller;


import com.multi.y2k4.service.transaction.PurchaseDetailsService;
import com.multi.y2k4.service.transaction.PurchaseService;
import com.multi.y2k4.service.transaction.SaleDetailsService;
import com.multi.y2k4.service.transaction.SaleService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final SaleDetailsService saleDetailsService;
    private final PurchaseDetailsService purchaseDetailsService;

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

        System.out.println(saleService.list(sale_id,emp_id,ac_id,order_date,due_date,status));
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
                            Model model){

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
        sale.setStatus(0);
        saleService.addSale(sale);
        int id = sale.getSale_id();
        for(int i = 0; i < stock_id.length; i++){
            saleDetails.get(i).setSale_id(id);
        }
        saleDetailsService.addSaleDetails(saleDetails);
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
                            @RequestParam Double[] price_per_unit){
        List<Sale> before_sale = saleService.list(sale_id,null,null,null,null,null);    //실질적으로 한개만 검색, 추후 document를 위해 수정 전 값을 검색
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
            saleDetail.setStock_id(stock_id[i]);
            saleDetail.setQty(qty[i]);
            saleDetail.setPrice_per_unit(price_per_unit[i]);
            saleDetail.setTotal_price(price_per_unit[i]*qty[i]);
            edit_saleDetail.add(saleDetail);
            total_price += saleDetail.getTotal_price();
        }

        edit_sale.setTotal_price(total_price);

        saleService.editSale(edit_sale);
        saleDetailsService.deleteSaleDetails(sale_id);
        saleDetailsService.addSaleDetails(edit_saleDetail);
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
                           Model model){
        double total_price = 0;
        List<PurchaseDetails> purchaseDetails = new ArrayList<>();

        for(int i = 0; i < stock_id.length; i++){
            PurchaseDetails purchaseDetail = new PurchaseDetails();
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setQty(qty[i]);
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
        purchase.setStatus(0);
        purchaseService.addPurchase(purchase);
        int id = purchase.getPurchase_id();

        for(int i = 0; i < stock_id.length; i++){
            purchaseDetails.get(i).setPurchase_id(id);
        }
        purchaseDetailsService.addPurchaseDetails(purchaseDetails);
        return true;

    }

    @GetMapping("/purchase/viewPurchaseDetails")
    public List<PurchaseDetails> purchaseDetailsList(@RequestParam Integer purchase_id){
        System.out.println(purchaseDetailsService.searchById(purchase_id));
        return purchaseDetailsService.searchById(purchase_id);
    }

    @PostMapping("/purchase/editPurchase")
    public boolean editPurchase(@RequestParam Integer purchase_id,
                                @RequestParam Integer emp_id,
                                @RequestParam Integer ac_id,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                @RequestParam Integer[] stock_id,
                                @RequestParam Integer[] qty,
                                @RequestParam Double[] price_per_unit){

        List<Purchase> before_purchase = purchaseService.list(purchase_id,null,null,null,null,null);    //실질적으로 한개만 검색, 추후 document를 위해 수정 전 값을 검색
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
            purchaseDetail.setStock_id(stock_id[i]);
            purchaseDetail.setQty(qty[i]);
            purchaseDetail.setPrice_per_unit(price_per_unit[i]);
            purchaseDetail.setTotal_price(price_per_unit[i]*qty[i]);
            edit_purchaseDetail.add(purchaseDetail);
            total_price += purchaseDetail.getTotal_price();
        }

        edit_purchase.setTotal_price(total_price);

        purchaseService.editPurchase(edit_purchase);
        purchaseDetailsService.deletePurchaseDetails(purchase_id);
        purchaseDetailsService.addPurchaseDetails(edit_purchaseDetail);
        return true;
    }
}
