package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleDetailsMapper;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaleDetailsService {
    private final SaleDetailsMapper saleDetailsMapper;

    public List<SaleDetails> searchById(int sale_id) {
        return saleDetailsMapper.searchById(sale_id);
    }

    public int addSaleDetails(List<SaleDetails> saleDetails) {
        return saleDetailsMapper.addSaleDetails(saleDetails);
    }

    public int deleteSaleDetails(int sale_id) {return saleDetailsMapper.deleteSaleDetails(sale_id);}

//--------------------대시보드--------------------
    public List<Map<String, Object>> selectSalesForDashboard() {
        return saleDetailsMapper.selectSalesForDashboard();
    }
}
