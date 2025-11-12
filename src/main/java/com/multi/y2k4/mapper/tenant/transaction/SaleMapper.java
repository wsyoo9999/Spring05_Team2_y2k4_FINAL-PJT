package com.multi.y2k4.mapper.tenant.transaction;

import com.multi.y2k4.vo.transaction.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SaleMapper {
    public int addSale(Sale sale);
    public List<Sale> list_all();
    public List<Sale> list( @Param("emp_id")Integer emp_id,
                            @Param("ac_id")Integer ac_id,
                            @Param("order_date") LocalDate order_date,
                            @Param("due_date") LocalDate due_date,
                            @Param("status") Integer status);
}
