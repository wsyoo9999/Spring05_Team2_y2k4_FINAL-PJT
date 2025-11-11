package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleMapper;
import com.multi.y2k4.vo.transaction.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleMapper saleMapper;

    public List<Sale> list_all() {
        return saleMapper.list_all();
    }
    public int addSale(Sale sale) {
        return saleMapper.addSale(sale);
    }
}
