package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleMapper;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<Sale> list(Integer sale_id,Integer emp_id, Integer ac_id, LocalDate order_date, LocalDate due_date, Integer status)
    {return saleMapper.list(sale_id,emp_id,ac_id,order_date,due_date,status);}

    public int editSale(Sale sale) {
        return saleMapper.editSale(sale);
    }
    public int editSaleStatus(Sale sale) {
        return saleMapper.editSaleStatus(sale);
    }
}
