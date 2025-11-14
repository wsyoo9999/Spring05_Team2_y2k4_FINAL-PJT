package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.InboundMapper;
import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class InboundService {
    private final InboundMapper inboundMapper;

    public List<Inbound> list_all() {
        return inboundMapper.list_all();
    }

    public int addInbound(Inbound inbound) {
        return inboundMapper.addInbound(inbound);
    }

    public Inbound selectInboundById(Integer inbound_id) { return inboundMapper.selectInboundById(inbound_id);  }
    public int updateInbound(Inbound inbound) {
        return inboundMapper.updateInbound(inbound);
    }

    // 검색 조건: inbound_date, stock_id, ac_id, emp_id, approval
    public List<Inbound> list(LocalDate inbound_date,
                              Integer stock_id,
                              Integer ac_id,
                              Integer emp_id,
                              Integer approval) {
        return inboundMapper.list(inbound_date, stock_id, ac_id, emp_id, approval);
    }
}