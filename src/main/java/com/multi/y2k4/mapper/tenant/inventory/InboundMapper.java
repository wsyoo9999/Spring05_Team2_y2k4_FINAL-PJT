package com.multi.y2k4.mapper.tenant.inventory;

import com.multi.y2k4.vo.inventory.Inbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface InboundMapper {
    public List<Inbound> list_all();
    public List<Inbound> list(Inbound inbound);
    public int addInbound(Inbound inbound);
    public Inbound selectInboundById(int inbound_id);
    public int updateInbound(Inbound inbound);
    public int deleteInbound(Inbound inbound);
}
