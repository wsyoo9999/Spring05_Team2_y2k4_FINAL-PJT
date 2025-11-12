package com.multi.y2k4.mapper.tenant.transaction;

import com.multi.y2k4.vo.transaction.Purchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PurchaseMapper {
    public int addPurchase(Purchase purchase);
    public List<Purchase> list_all();
    public List<Purchase> list(
                            @Param("purchase_id") Integer purchase_id,
                            @Param("emp_id")Integer emp_id,
                            @Param("ac_id")Integer ac_id,
                            @Param("order_date") LocalDate order_date,
                            @Param("del_date") LocalDate del_date,
                            @Param("status") Integer status);
}
