package com.multi.y2k4.mapper.tenant.transaction;


import com.multi.y2k4.vo.transaction.PurchaseDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseDetailsMapper {
    public List<PurchaseDetails> searchById(@Param("purchase_id") int purchase_id);
    public int addPurchaseDetails(@Param("purchaseDetails") List<PurchaseDetails> purchaseDetails);
}
