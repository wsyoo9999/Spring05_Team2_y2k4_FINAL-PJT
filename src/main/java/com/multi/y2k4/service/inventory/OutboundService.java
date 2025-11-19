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
        // 기존 출고 정보 조회
        Outbound oldOutbound = outboundMapper.selectOutboundById(outbound.getOutbound_id());

        if (oldOutbound == null) {
            return 0;
        }

        // 승인 상태 변경 감지
        Integer oldApproval = oldOutbound.getApproval();
        Integer newApproval = outbound.getApproval();

        // 대기(0) >> 승인(1): 재고 감소
        if (oldApproval != null && oldApproval == 0 &&
                newApproval != null && newApproval == 1) {
            updateStockQuantity(oldOutbound.getStock_id(), -oldOutbound.getOutbound_qty());
        }

        // 승인(1) → 대기(0): 재고 증가 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 0) {
            updateStockQuantity(oldOutbound.getStock_id(), oldOutbound.getOutbound_qty());
        }

        // 승인(1) > 반려(2): 재고 증가 (취소)
        else if (oldApproval != null && oldApproval == 1 &&
                newApproval != null && newApproval == 2) {
            updateStockQuantity(oldOutbound.getStock_id(), oldOutbound.getOutbound_qty());
        }

        // 출고 정보 수정
        return outboundMapper.updateOutbound(outbound);
    }
    // 재고 수량
    private void updateStockQuantity(Integer stockId, Integer qtyChange) {
        Stock stock = stockService.selectStockById(stockId);

        if (stock != null) {
            int newQty = stock.getQty() + qtyChange;

            stock.setQty(newQty);
            stockService.updateStock(stock);
            System.out.println("재고 수량 변경: stock_id=" + stockId +
                    ", 변경량=" + qtyChange +
                    ", 새 수량=" + newQty);
        }
    }

    public int deleteOutbound(Outbound outbound) {return outboundMapper.deleteOutbound(outbound);}
    // 검색 조건: outbound_date, stock_id, ac_id, emp_id, approval
    public List<Outbound> list(Outbound outbound) {
        return outboundMapper.list(outbound);
    }
}
