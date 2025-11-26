package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleDetailsMapper;
import com.multi.y2k4.mapper.tenant.transaction.SaleMapper;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleMapper saleMapper;
    private final SaleDetailsMapper saleDetailsMapper;

    public List<Sale> list_all() {
        return saleMapper.list_all();
    }
    public int addSale(Sale sale) {
        return saleMapper.addSale(sale);
    }
    public List<Sale> list(Integer sale_id,Integer emp_id, Integer ac_id, LocalDate order_date, LocalDate due_date, Integer status)
    {return saleMapper.list(sale_id,emp_id,ac_id,order_date,due_date,status);}

    public int editSale(Sale sale) {
        return saleMapper.editSale(sale);
    }
    public int editSaleStatus(Sale sale) {
        return saleMapper.editSaleStatus(sale);
    }

    @Transactional
    public int deleteSale(Integer sale_id) {
        saleDetailsMapper.deleteSaleDetails(sale_id);
        return saleMapper.deleteSale(sale_id);
    }

    public Sale searchById(Integer sale_id) {
        return saleMapper.searchById(sale_id);
    }

//--------------------대시보드--------------------
    public List<Sale> getMonthlySales() {
        return saleMapper.selectMonthlySales();
    }

    public Map<String, Long> getThisAndLastMonthSaleTotal() {
        Map<String, Object> raw = saleMapper.selectThisAndLastMonthSaleTotal();

        if (raw == null) {
            raw = new HashMap<>();
        }

        long thisMonth = toLong(raw.getOrDefault("this_month_sale", 0L));  // 0L = long 타입 0
        long lastMonth = toLong(raw.getOrDefault("last_month_sale", 0L));

        Map<String, Long> result = new HashMap<>();
        result.put("this_month_sale", thisMonth);
        result.put("last_month_sale", lastMonth);

        return result;
    }

    private long toLong(Object value){
        if (value == null) return 0L;

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        } else {
            return Long.parseLong(value.toString());
        }
    }

    // 컨트롤러에서 바로 쓰기 편하게 분리 메서드도 만들 수 있음
    public long getThisMonthSaleTotal() {
        return getThisAndLastMonthSaleTotal().getOrDefault("this_month_sale", 0L);
    }

    public long getLastMonthSaleTotal() {
        return getThisAndLastMonthSaleTotal().getOrDefault("last_month_sale", 0L);
    }
}
