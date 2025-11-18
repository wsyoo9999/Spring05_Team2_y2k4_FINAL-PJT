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

        // 대기(0) >> 승인(1): 재고 증가
        if (oldApproval != null && oldApproval == 0 &&
                newApproval != null && newApproval == 1) {
            updateStockQuantity(oldInbound.getStock_id(), oldInbound.getInbound_qty());
        }

        // 승인(1) → 대기(0): 재고 감소 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 0) {
            updateStockQuantity(oldInbound.getStock_id(), -oldInbound.getInbound_qty());
        }

        // 승인(1) > (반려2): 재고 감소 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 2) {
            updateStockQuantity(oldInbound.getStock_id(), -oldInbound.getInbound_qty());
        }

        // 입고 정보 수정
        return inboundMapper.updateInbound(inbound);
    }
    // 재고 수량
    private void updateStockQuantity(Integer stockId, Integer qtyChange) {
        Stock stock = stockService.selectStockById(stockId);

        if (stock != null) {
            int newQty = stock.getQty() + qtyChange;

            if (newQty < 0) {
                throw new IllegalStateException("재고 수량이 음수가 될 수 없습니다.");
            }

            stock.setQty(newQty);
            stockService.updateStock(stock);
            System.out.println("재고 수량 변경: stock_id=" + stockId +
                    ", 변경량=" + qtyChange +
                    ", 새 수량=" + newQty);
        }
    }
    public int deleteInbound(Inbound inbound) {return inboundMapper.deleteInbound(inbound);}

    // 검색 조건: inbound_date, stock_id, ac_id, emp_id, approval
    public List<Inbound> list(LocalDate inbound_date,
                              Integer stock_id,
                              Integer ac_id,
                              Integer emp_id,
                              Integer approval) {
        return inboundMapper.list(inbound_date, stock_id, ac_id, emp_id, approval);
    }
}