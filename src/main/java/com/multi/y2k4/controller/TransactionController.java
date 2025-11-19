package com.multi.y2k4.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import com.multi.y2k4.service.finance.UnpaidService;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.service.transaction.*;
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
    private final TransactionService transactionService;

    @GetMapping("/sale/list")
    public List<Sale> saleList(@RequestParam(required = false) Integer sale_id,
                               @RequestParam(required = false) Integer emp_id,
                               @RequestParam(required = false) Integer ac_id,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                               @RequestParam(required = false) Double total_price,
                               @RequestParam(required = false) Integer status) {


        return saleService.list(sale_id,emp_id,ac_id,order_date,due_date,status);
    }

    @GetMapping("/sale/searchById")
    public Sale searchById(@RequestParam Integer sale_id){
        return saleService.searchById(sale_id);
    }

    @PostMapping("/sale/add")
    public boolean addSale(@RequestParam(required = false) Integer emp_id,
                           @RequestParam(required = false) Integer ac_id,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                           @RequestParam Integer[] stock_id,
                           @RequestParam Integer[] qty,
                           @RequestParam Double[] price_per_unit,
                           Model model,
                           HttpSession httpSession) throws JsonProcessingException {

        return transactionService.addSale(
                emp_id, ac_id, order_date, due_date,
                stock_id, qty, price_per_unit,
                httpSession
        );
    }

    @GetMapping("/sale/viewSaleDetails")
    public List<SaleDetails> saleDetailsList(@RequestParam Integer sale_id){

        return saleDetailsService.searchById(sale_id);
    }

    @PostMapping("/sale/editSale")
    public boolean editSale(@RequestParam Integer sale_id,
                            @RequestParam(required = false) Integer emp_id,
                            @RequestParam(required = false) Integer ac_id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                            @RequestParam Integer[] stock_id,
                            @RequestParam Integer[] qty,
                            @RequestParam Double[] price_per_unit,
                            HttpSession httpSession) throws JsonProcessingException {

        return transactionService.editSale(
                sale_id, emp_id, ac_id, order_date, due_date,
                stock_id, qty, price_per_unit,
                httpSession
        );
    }

    @PostMapping("/sale/editSaleStatus")
    public boolean editSaleStatus(@RequestParam Integer sale_id,
                                  @RequestParam Integer status) {

        return transactionService.editSaleStatus(sale_id, status);
    }

    @GetMapping("/purchase/list")
    public List<Purchase> purchaseList( @RequestParam(required = false) Integer purchase_id,
                                        @RequestParam(required = false) Integer emp_id,
                                        @RequestParam(required = false) Integer ac_id,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                                        @RequestParam(required = false) Integer status,
                                       Model model) {


        return purchaseService.list(purchase_id,emp_id,ac_id,order_date,del_date,status);
    }

    @PostMapping("/purchase/add")
    public boolean addPurchase(@RequestParam Integer emp_id,
                               @RequestParam Integer ac_id,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                               @RequestParam Integer[] stock_id,
                               @RequestParam Integer[] qty,
                               @RequestParam Double[] price_per_unit,
                               Model model,
                               HttpSession httpSession) throws JsonProcessingException {

        return transactionService.addPurchase(
                emp_id, ac_id, order_date, del_date,
                stock_id, qty, price_per_unit,
                httpSession
        );
    }

    @GetMapping("/purchase/viewPurchaseDetails")
    public List<PurchaseDetails> purchaseDetailsList(@RequestParam Integer purchase_id){

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
                                @RequestParam Double[] price_per_unit,
                                HttpSession httpSession) throws JsonProcessingException {

        return transactionService.editPurchase(
                purchase_id, emp_id, ac_id, order_date, del_date,
                stock_id, qty, price_per_unit,
                httpSession
        );
    }

    @PostMapping("/purchase/editPurchaseStatus")
    public boolean editPurchaseStatus(@RequestParam Integer purchase_id,
                                      @RequestParam Integer[] pd_id,
                                      @RequestParam Integer[] qty) {

        return transactionService.editPurchaseStatus(purchase_id, pd_id, qty);
    }
}
