package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.SpendMapper;
import com.multi.y2k4.vo.finance.Spend;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendService {

    private final SpendMapper spendMapper;

    public int addSpend(Spend spend){
        return spendMapper.addSpend(spend);
    };

    public Spend searchById(Long spend_id){
        return spendMapper.searchById(spend_id);
    };

    public List<Spend> list_all(){
        return spendMapper.list_all();
    };

    public List<Spend> list(Long spend_id,Integer cat_id, Integer tb_id,LocalDateTime from_date,LocalDateTime to_date){
        return spendMapper.list(spend_id,cat_id,tb_id,from_date,to_date);
    };

    public int editSpend(Spend spend){
        return spendMapper.editSpend(spend);
    };

    public int deleteSpend(Long spend_id){
        return spendMapper.deleteSpend(spend_id);
    };
}
