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
    private final StockService stockService;

    public List<Inbound> list_all() {
        return inboundMapper.list_all();
    }

    public int addInbound(Inbound inbound) {
        return inboundMapper.addInbound(inbound);
    }

    public Inbound selectInboundById(Integer inbound_id) { return inboundMapper.selectInboundById(inbound_id);  }

    public int updateInbound(Inbound inbound) {
        // 기존 입고 정보 조회
        Inbound oldInbound = inboundMapper.selectInboundById(inbound.getInbound_id());

        if (oldInbound == null) {
            return 0;
        }

        // 승인 상태 변경 감지
        Integer oldApproval = oldInbound.getApproval();
        Integer newApproval = inbound.getApproval();

        // 대기(0) > 승인(1): 재고 증가
        if (oldApproval != null && oldApproval == 0 &&
                newApproval != null && newApproval == 1) {
            stockService.manageAcquiredAty(
                    oldInbound.getStock_id(),
                    1,  // 증가
                    oldInbound.getInbound_qty()
            );
        }

        // 승인(1) > 대기(0): 재고 감소 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 0) {
            stockService.manageAcquiredAty(
                    oldInbound.getStock_id(),
                    2,  // 감소
                    oldInbound.getInbound_qty()
            );
        }

        // 승인(1) > 반려(2): 재고 감소 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 2) {
            stockService.manageAcquiredAty(
                    oldInbound.getStock_id(),
                    2,  // 감소
                    oldInbound.getInbound_qty()
            );
        }

        // 입고 정보 수정
        return inboundMapper.updateInbound(inbound);
    }

    // updateStockQuantity 메서드는 삭제
    public int deleteInbound(Inbound inbound) {return inboundMapper.deleteInbound(inbound);}

    // 검색 조건: inbound_date, stock_id, ac_id, emp_id, approval
    public List<Inbound> list(Inbound inbound) {
        return inboundMapper.list(inbound);
    }
}