package com.multi.y2k4.mapper.tenant.inventory;

import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Outbound;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface OutboundMapper {
    public List<Outbound> list_all();
    public List<Outbound> list(@Param("outbound_date") LocalDate outbound_date,
                              @Param("stock_id")Integer stock_id,
                              @Param("ac_id")Integer ac_id,
                              @Param("emp_id")Integer emp_id,
                              @Param("approval")Integer approval);
    public int addOutbound(Outbound outbound);
    public int updateOutbound(Outbound outbound);
}
