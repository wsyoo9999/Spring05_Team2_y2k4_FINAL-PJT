package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.ProfitMapper;
import com.multi.y2k4.vo.finance.Profit;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfitService {

    private final ProfitMapper profitMapper;

    public int addProfit(Profit profit) {
        return profitMapper.addProfit(profit);
    }
    public Profit searchById(Long profit_id){
        return profitMapper.searchById(profit_id);
    };

    public List<Profit> list_all(){
        return profitMapper.list_all();
    };

    public List<Profit> list(Long profit_id,Integer cat_id,Integer tb_id,LocalDateTime from_date,LocalDateTime to_date){
        return profitMapper.list(profit_id,cat_id,tb_id,from_date,to_date);
    };

    public int editProfit(Profit profit){
        return profitMapper.editProfit(profit);
    };

    public int deleteProfit(Long profit_id){
        return profitMapper.deleteProfit(profit_id);
    };
}