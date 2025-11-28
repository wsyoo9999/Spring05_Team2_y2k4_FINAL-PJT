package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.OutboundMapper;
import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Outbound;
import com.multi.y2k4.vo.inventory.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboundService {
    private final OutboundMapper outboundMapper;
    private final StockService stockService;


    public List<Outbound> list_all() {
        return outboundMapper.list_all();
    }

    public int addOutbound(Outbound outbound) {
        return outboundMapper.addOutbound(outbound);
    }

    public Outbound selectOutboundById(Integer outbound_id) { return outboundMapper.selectOutboundById(outbound_id);  }

    public int updateOutbound(Outbound outbound) {
        return outboundMapper.updateOutbound(outbound);
    }


    public int deleteOutbound(Outbound outbound) {return outboundMapper.deleteOutbound(outbound);}
    // 검색 조건: outbound_date, stock_id, ac_id, emp_id, approval
    public List<Outbound> list(Outbound outbound) {
        return outboundMapper.list(outbound);
    }
}
