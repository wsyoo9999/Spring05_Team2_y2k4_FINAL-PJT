package com.multi.y2k4.mapper.tenant.transaction;


import com.multi.y2k4.vo.transaction.PurchaseDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseDetailsMapper {
    public List<PurchaseDetails> searchById(@Param("purchase_id") int purchase_id);
    public int addPurchaseDetails(@Param("purchaseDetails") List<PurchaseDetails> purchaseDetails);
    public int deletePurchaseDetails(@Param("purchase_id") int purchase_id);

    int editPurchaseDetailsQTY(@Param("pd_id") int pd_id, @Param("qty") int qty);

    public List<Map<String, Object>> selectPurchasesForDashboard();

}
