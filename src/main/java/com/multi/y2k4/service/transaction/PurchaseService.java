package com.multi.y2k4.service.transaction;


import com.multi.y2k4.mapper.tenant.transaction.PurchaseMapper;
import com.multi.y2k4.vo.transaction.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    public int addPurchase(Purchase purchase){
        return purchaseMapper.addPurchase(purchase);
    }
    public List<Purchase> list_all(){
        return purchaseMapper.list_all();
    }
}
