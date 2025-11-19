package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.StockMapper;
import com.multi.y2k4.vo.inventory.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Stock selectStockById(Integer stock_id) { return stockMapper.selectStockById(stock_id);  }
    public int updateStock(Stock stock) {
        return stockMapper.updateStock(stock);
    }

    public int deleteStock(Stock stock) {return stockMapper.deleteStock(stock);}

    public List<Stock> list(Stock stock) {
        return stockMapper.list(stock);
    }
}