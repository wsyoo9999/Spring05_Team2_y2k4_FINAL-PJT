package com.multi.y2k4.controller;

import com.multi.y2k4.service.transaction.SaleDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final SaleDetailsService saleDetailsService;

    @GetMapping("/showChart/saleDonut")
    public List<Map<String, Object>> showChart() {
        return saleDetailsService.selectSalesForDashboard();
    }
}
