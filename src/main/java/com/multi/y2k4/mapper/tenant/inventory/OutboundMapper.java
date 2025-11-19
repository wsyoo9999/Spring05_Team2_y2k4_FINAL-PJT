package com.multi.y2k4.mapper.tenant.inventory;

import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Outbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OutboundMapper {
    public List<Outbound> list_all();
    public List<Outbound> list(Outbound outbound);
    public int addOutbound(Outbound outbound);
    public Outbound selectOutboundById(int outbound_id);
    public int updateOutbound(Outbound outbound);
    public int deleteOutbound(Outbound outbound);
}
