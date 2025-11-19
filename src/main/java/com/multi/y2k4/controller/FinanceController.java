package com.multi.y2k4.controller;

import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import com.multi.y2k4.service.finance.UnpaidService;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
import com.multi.y2k4.vo.finance.Unpaid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    // Service 계층 주입 (DB SELECT 시 사용)
    private final ProfitService profitService;
    private final SpendService spendService;
    private final UnpaidService unpaidService;

    @GetMapping("/profit/list")
    public List<Profit> profitList(@RequestParam(required = false) Long profit_id,
                                   @RequestParam(required = false) Integer cat_id,
                                   @RequestParam(required = false) Integer tb_id,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from_date,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to_date){
        return profitService.list(profit_id, cat_id, tb_id, from_date.atStartOfDay(), to_date.atStartOfDay());
    }

    @GetMapping("/profit/searchById")
    public Profit profitSearchById(@RequestParam Long profit_id){
        return profitService.searchById(profit_id);
    }


    @GetMapping("/spend/list")
    public List<Spend> spendList(@RequestParam(required = false) Long spend_id,
                                 @RequestParam(required = false) Integer cat_id,
                                 @RequestParam(required = false) Integer tb_id,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from_date,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to_date){
        return spendService.list(spend_id, cat_id, tb_id, from_date.atStartOfDay(), to_date.atStartOfDay());
    }

    @GetMapping("/spend/searchById")
    public Spend spendSearchById(@RequestParam Long spend_id){
        return spendService.searchById(spend_id);
    }

    @GetMapping("/unpaid/list")
    public List<Unpaid> unpaidList(@RequestParam(required = false) Integer cat_id,
                                   @RequestParam(required = false) Integer tb_id,
                                   @RequestParam(required = false) Integer type,
                                   @RequestParam(required = false) Integer status){
        return unpaidService.list(cat_id, tb_id, type, status );
    }

    @GetMapping("/unpaid/searchById")
    public Unpaid unpaidSearchById(@RequestParam Long unpaid_id){
        return unpaidService.searchById(unpaid_id);
    }



}