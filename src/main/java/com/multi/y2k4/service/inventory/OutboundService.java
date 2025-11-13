package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.OutboundMapper;
import com.multi.y2k4.vo.inventory.Outbound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboundService {
    private final OutboundMapper outboundMapper;

    public List<Outbound> list_all() {
        return outboundMapper.list_all();
    }

    public int addOutbound(Outbound outbound) {
        return outboundMapper.addOutbound(outbound);
    }

    public int updateOutbound(Outbound outbound) {
        return outboundMapper.updateOutbound(outbound);
    }

    // 검색 조건: outbound_date, stock_id, ac_id, emp_id, approval
    public List<Outbound> list(LocalDate outbound_date,
                               Integer stock_id,
                               Integer ac_id,
                               Integer emp_id,
                               Integer approval) {
        return outboundMapper.list(outbound_date, stock_id, ac_id, emp_id, approval);
    }
}
