package com.multi.y2k4.controller;

import com.multi.y2k4.mapper.tenant.transaction.PurchaseDetailsMapper;
import com.multi.y2k4.mapper.tenant.transaction.PurchaseMapper;
import com.multi.y2k4.service.transaction.PurchaseDetailsService;
import com.multi.y2k4.service.transaction.SaleDetailsService;
import com.multi.y2k4.service.transaction.SaleService;
import com.multi.y2k4.vo.transaction.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final SaleDetailsService saleDetailsService;
    private final SaleService saleService;
    private final PurchaseDetailsService purchaseDetailsService;

    @GetMapping("/showChart/saleDonut")
    public List<Map<String, Object>> saleDonut() {
        return saleDetailsService.selectSalesForDashboard();
    }

    @GetMapping("/showChart/purchaseDonut")
    public List<Map<String, Object>> purchaseDonut() {return purchaseDetailsService.selectPurchasesForDashboard(); }

    @GetMapping("/showChart/saleLine")
    public List<Sale> saleLine() {return saleService.getMonthlySales();}

    @GetMapping("/showKpi/saleThisAndLast")
    public Map<String, Long> getThisAndLastMonthSaleKpi() {
        Map<String, Long> totals = saleService.getThisAndLastMonthSaleTotal();
        long thisMonth = totals.getOrDefault("this_month_sale", 0L);
        long lastMonth = totals.getOrDefault("last_month_sale", 0L);

        Map<String, Long> result = new HashMap<>();
        result.put("thisMonthSale", thisMonth);
        result.put("lastMonthSale", lastMonth);
        return result;
    }
}
