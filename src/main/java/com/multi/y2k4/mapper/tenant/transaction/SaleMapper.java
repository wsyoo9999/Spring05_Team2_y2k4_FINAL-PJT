package com.multi.y2k4.mapper.tenant.transaction;

import com.multi.y2k4.vo.transaction.Sale;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SaleMapper {
    public int addSale(Sale sale);
    public List<Sale> list_all();
}
