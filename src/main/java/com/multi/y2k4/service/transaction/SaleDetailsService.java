package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleDetailsMapper;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
