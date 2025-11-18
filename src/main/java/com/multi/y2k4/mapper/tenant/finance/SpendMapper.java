package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Spend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SpendMapper {

    int addSpend(Spend spend);

    Spend searchById(@Param("spend_id") Long spend_id);

    List<Spend> list_all();

    List<Spend> list(
            @Param("spend_id") Long spend_id,
            @Param("cat_id") Integer cat_id,
            @Param("tb_id") Integer tb_id,
            @Param("from_date") LocalDateTime from_date,
            @Param("to_date") LocalDateTime to_date
    );

    int editSpend(Spend spend);

    int deleteSpend(@Param("spend_id") Long spend_id);
}
