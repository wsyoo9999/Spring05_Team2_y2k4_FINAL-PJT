package com.multi.y2k4.controller;


import com.multi.y2k4.vo.transaction.Sale;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @GetMapping("/sale/list")
    public List<Sale> list(@RequestParam(required = false) Integer emp_id,
                       @RequestParam(required = false) Integer client_id,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate order_date,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate del_date,
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

        return saleList;
    }

}
