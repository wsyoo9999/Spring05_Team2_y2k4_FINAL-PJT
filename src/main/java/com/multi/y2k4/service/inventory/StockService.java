package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.StockMapper;
import com.multi.y2k4.mapper.tenant.transaction.SaleMapper;
import com.multi.y2k4.vo.inventory.Stock;
import com.multi.y2k4.vo.transaction.Sale;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    public List<Stock> list_all() {
        return stockMapper.list_all();
    }

    public int addStock(Stock stock) {
        return stockMapper.addStock(stock);
    }
    public int updateStock(Stock stock) {
        return stockMapper.updateStock(stock);
    }

    public List<Stock> list(String stock_name, Integer qty, Integer unit_price, Integer location, Integer type) {
        return stockMapper.list(stock_name, qty, unit_price, location, type);
    }
}