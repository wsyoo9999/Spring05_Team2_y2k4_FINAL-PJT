package com.multi.y2k4.controller;


import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.Sale;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @GetMapping("/sale/list")
    public List<Sale> saleList(@RequestParam(required = false) Integer emp_id,
                       @RequestParam(required = false) Integer client_id,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                       @RequestParam(required = false) String status,
                       Model model) {

        //테스트를 위한 수동 리스트 값 생성
        List<Sale> saleList = new ArrayList<>();
        Sale sale = new Sale();
        sale.setEmp_id(1);
        sale.setClient_id(11);
        sale.setOrder_date(LocalDate.parse("2025-11-03"));
        sale.setDue_date(LocalDate.parse("2025-11-03"));
        sale.setStatus("good");
        saleList.add(sale);
        Sale sale2 = new Sale();
        sale2.setEmp_id(1);
        sale2.setClient_id(11);
        sale2.setOrder_date(LocalDate.parse("2025-11-03"));
        sale2.setDue_date(LocalDate.parse("2025-11-03"));
        sale2.setStatus("good");
        saleList.add(sale2);

        System.out.println(emp_id);
        System.out.println(client_id);
        System.out.println(order_date);
        System.out.println(due_date);
        System.out.println(status);
        return saleList;
    }

    @PostMapping("/sale/add")
    public boolean addSale(@RequestParam Integer emp_id,
                          @RequestParam Integer client_id,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
                          @RequestParam Integer[] item_id,
                          @RequestParam Integer[] qty,
                          Model model){
        System.out.println("emp_id : "+emp_id);
        System.out.println("client_id : "+client_id);
        System.out.println("order_date : "+order_date);
        System.out.println("due_date : "+due_date);
        System.out.println("item_id : "+ Arrays.toString(item_id));
        System.out.println("qty : "+ Arrays.toString(qty));
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
        List<Purchase> purchaseList = new ArrayList<>();
        Purchase purchase1 = new Purchase();
        purchase1.setEmp_id(2);
        purchase1.setClient_id(22);
        purchase1.setOrder_date(LocalDate.parse("2025-11-04"));
        purchase1.setDel_date(LocalDate.parse("2025-11-04"));
        purchase1.setStatus("bad");
        purchaseList.add(purchase1);
        Purchase purchase2 = new Purchase();
        purchase2.setEmp_id(2);
        purchase2.setClient_id(22);
        purchase2.setOrder_date(LocalDate.parse("2025-11-03"));
        purchase2.setDel_date(LocalDate.parse("2025-11-03"));
        purchase2.setStatus("bad");
        purchaseList.add(purchase2);

        System.out.println(emp_id);
        System.out.println(client_id);
        System.out.println(order_date);
        System.out.println(del_date);
        System.out.println(status);
        return purchaseList;
    }

    @PostMapping("/sale/add")
    public boolean addPurchase(@RequestParam Integer emp_id,
                           @RequestParam Integer client_id,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
                           @RequestParam Integer[] item_id,
                           @RequestParam Integer[] qty,
                           Model model){
        System.out.println("emp_id : "+emp_id);
        System.out.println("client_id : "+client_id);
        System.out.println("order_date : "+order_date);
        System.out.println("due_date : "+del_date);
        System.out.println("item_id : "+ Arrays.toString(item_id));
        System.out.println("qty : "+ Arrays.toString(qty));
        return true;
    }

}
