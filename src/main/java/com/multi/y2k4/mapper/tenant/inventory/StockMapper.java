package com.multi.y2k4.mapper.tenant.inventory;

import com.multi.y2k4.vo.inventory.Stock;
import com.multi.y2k4.vo.transaction.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockMapper {
    public List<Stock> list_all();
    public List<Stock> list( @Param("stock_name")String stock_name,
                             @Param("qty")Integer qty,
                             @Param("unit_price")Integer unit_price,
                             @Param("location")String location,
                             @Param("type")Integer type);
    public int addStock(Stock stock);
    Stock selectStockById(int stock_id);
    public int updateStock(Stock stock);
    public int deleteStock(Stock stock);
}

