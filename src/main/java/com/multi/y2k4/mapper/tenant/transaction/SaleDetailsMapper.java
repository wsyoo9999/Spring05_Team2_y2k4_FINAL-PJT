package com.multi.y2k4.mapper.tenant.transaction;


import com.multi.y2k4.vo.transaction.SaleDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SaleDetailsMapper {

    public List<SaleDetails> searchById(@Param("sale_id") int sale_id);

    public int addSaleDetails(@Param("saleDetails") List<SaleDetails> saleDetails);

    public int deleteSaleDetails(@Param("sale_id") int sale_id);

    public List<Map<String, Object>> selectSalesForDashboard();
}
