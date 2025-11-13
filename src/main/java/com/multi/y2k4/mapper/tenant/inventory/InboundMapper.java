package com.multi.y2k4.mapper.tenant.inventory;

import com.multi.y2k4.vo.inventory.Inbound;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface InboundMapper {
    public List<Inbound> list_all();
    public List<Inbound> list(@Param("inbound_date") LocalDate inbound_date,
                              @Param("stock_id")Integer stock_id,
                              @Param("ac_id")Integer ac_id,
                              @Param("emp_id")Integer emp_id,
                              @Param("approval")Integer approval);
    public int addInbound(Inbound inbound);
    public int updateInbound(Inbound inbound);
}
