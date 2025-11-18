package com.multi.y2k4.controller;

import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    // Service 계층 주입 (DB SELECT 시 사용)
    private final ProfitService profitService;
    private final SpendService spendService;



}