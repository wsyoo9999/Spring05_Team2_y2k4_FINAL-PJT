package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Profit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProfitMapper {

    int addProfit(Profit profit);

    Profit searchById(@Param("profit_id") Long profit_id);

    List<Profit> list_all();

    List<Profit> list(
            @Param("profit_id") Long profit_id,
            @Param("cat_id") Integer cat_id,
            @Param("tb_id") Integer tb_id,
            @Param("from_date") LocalDateTime from_date,
            @Param("to_date") LocalDateTime to_date
    );

    int editProfit(Profit profit);

    int deleteProfit(@Param("profit_id") Long profit_id);
}
