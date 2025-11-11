package com.multi.y2k4.mapper.tenant.transaction;

import com.multi.y2k4.vo.transaction.Purchase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PurchaseMapper {
    public int addPurchase(Purchase purchase);
    public List<Purchase> list_all();
}
