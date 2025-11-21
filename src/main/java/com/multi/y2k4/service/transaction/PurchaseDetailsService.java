package com.multi.y2k4.service.transaction;


import com.multi.y2k4.mapper.tenant.transaction.PurchaseDetailsMapper;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseDetailsService {
    private final PurchaseDetailsMapper purchaseDetailsMapper;

    public List<PurchaseDetails> searchById(int purchase_id){
        return purchaseDetailsMapper.searchById(purchase_id);
    }

    public int addPurchaseDetails(List<PurchaseDetails> purchaseDetails){
        return purchaseDetailsMapper.addPurchaseDetails(purchaseDetails);
    }

    public int deletePurchaseDetails(int purchase_id){
        return purchaseDetailsMapper.deletePurchaseDetails(purchase_id);
    }
    public int editPurchaseDetailsQTY(int pd_id, int qty){
        return purchaseDetailsMapper.editPurchaseDetailsQTY(pd_id,qty);
    }
}
