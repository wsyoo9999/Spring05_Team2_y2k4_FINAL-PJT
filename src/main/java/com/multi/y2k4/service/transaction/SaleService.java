package com.multi.y2k4.service.transaction;

import com.multi.y2k4.mapper.tenant.transaction.SaleDetailsMapper;
import com.multi.y2k4.mapper.tenant.transaction.SaleMapper;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleMapper saleMapper;
    private final SaleDetailsMapper saleDetailsMapper;

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

    @Transactional
    public int deleteSale(Integer sale_id) {
        saleDetailsMapper.deleteSaleDetails(sale_id);
        return saleMapper.deleteSale(sale_id);
    }

    public Sale searchById(Integer sale_id) {
        return saleMapper.searchById(sale_id);
    }
}
