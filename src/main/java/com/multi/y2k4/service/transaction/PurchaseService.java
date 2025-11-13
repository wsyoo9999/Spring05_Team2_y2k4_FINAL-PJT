package com.multi.y2k4.service.transaction;


import com.multi.y2k4.mapper.tenant.transaction.PurchaseMapper;
import com.multi.y2k4.vo.transaction.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<Purchase> list(Integer purchase_id,Integer emp_id, Integer ac_id, LocalDate order_date, LocalDate del_date, Integer status)
        {return purchaseMapper.list(purchase_id,emp_id,ac_id,order_date,del_date,status);}

    public int editPurchase(Purchase purchase){
        return purchaseMapper.editPurchase(purchase);
    }
    public int editPurchaseStatus(Purchase purchase){
        return purchaseMapper.editPurchaseStatus(purchase);}
}
