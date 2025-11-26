package com.multi.y2k4.mapper.tenant.transaction;

import com.multi.y2k4.vo.transaction.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface SaleMapper {
    public int addSale(Sale sale);
    public List<Sale> list_all();
    public List<Sale> list( @Param("sale_id") Integer sale_id,
                            @Param("emp_id")Integer emp_id,
                            @Param("ac_id")Integer ac_id,
                            @Param("order_date") LocalDate order_date,
                            @Param("due_date") LocalDate due_date,
                            @Param("status") Integer status);

    public int editSale(Sale sale);
    public int editSaleStatus(Sale sale);

    public int deleteSale(@Param("sale_id")Integer sale_id);

    public Sale searchById(@Param("sale_id")Integer sale_id);

    public List<Sale> selectMonthlySales();
    public Map<String, Object> selectThisAndLastMonthSaleTotal();


}
