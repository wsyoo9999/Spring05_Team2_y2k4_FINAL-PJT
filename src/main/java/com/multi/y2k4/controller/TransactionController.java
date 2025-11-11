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
                       @RequestParam(required = false) String status,
                       Model model) {

        //테스트를 위한 수동 리스트 값 생성


        return saleService.list_all();
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
        ;
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

    @GetMapping("/purchase/list")
    public List<Purchase> purchaseList(@RequestParam(required = false) Integer emp_id,
                                       @RequestParam(required = false) Integer client_id,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                       @RequestParam(required = false) String status,
                                       Model model) {

        //테스트를 위한 수동 리스트 값 생성

        return purchaseService.list_all();
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

}
