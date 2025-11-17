package com.multi.y2k4.service.production;

import com.multi.y2k4.mapper.tenant.production.ProductionMapper;
import com.multi.y2k4.vo.production.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionService {

    private final ProductionMapper productionMapper;

    // --- 작업지시서 (Work Order) ---

    public List<WorkOrder> getWorkOrderList(String order_status, String stock_name, String start_date, String due_date) {
        return productionMapper.getWorkOrderList(order_status, stock_name, start_date, due_date);
    }

    public WorkOrder getWorkOrderDetail(Long work_order_id) {
        return productionMapper.getWorkOrderDetail(work_order_id);
    }

    public List<Lot> getWorkOrderLots(Long work_order_id) {
        return productionMapper.getWorkOrderLots(work_order_id);
    }

    public List<Defect> getWorkOrderDefects(Long work_order_id) {
        return productionMapper.getWorkOrderDefects(work_order_id);
    }

    @Transactional // 쓰기 작업은 별도로 트랜잭션 허용
    public boolean addWorkOrder(WorkOrder workOrder) {
        return productionMapper.addWorkOrder(workOrder) > 0;
    }

    @Transactional
    public boolean deleteWorkOrder(Long work_order_id) {
        // 1. [자식의 자식] 불량 내역(Defect) 먼저 삭제
        // (Lot가 삭제되면 불량 내역의 lot_id가 갈 곳을 잃으므로 먼저 삭제해야 함)
        productionMapper.deleteDefectsByWorkOrderId(work_order_id);

        // 2. [자식] 생산 실적(Lot) 삭제
        // (작업지시서가 삭제되면 Lot의 work_order_id가 갈 곳을 잃으므로 삭제)
        productionMapper.deleteLotsByWorkOrderId(work_order_id);

        // 3. [부모] 작업지시서(WorkOrder) 최종 삭제
        return productionMapper.deleteWorkOrder(work_order_id) > 0;
    }

    // --- 자재 명세서 (BOM) ---

    public List<BOM> getBOMList(String parent_stock_name, String child_stock_name) {
        return productionMapper.getBOMList(parent_stock_name, child_stock_name);
    }

    public BOM getBOMById(Long bom_id) {
        return productionMapper.getBOMById(bom_id);
    }

    @Transactional
    public boolean addBOM(BOM bom) {
        return productionMapper.addBOM(bom) > 0;
    }

    @Transactional
    public boolean updateBOM(BOM bom) {
        return productionMapper.updateBOM(bom) > 0;
    }

    @Transactional
    public boolean deleteBOM(Long bom_id) {
        return productionMapper.deleteBOM(bom_id) > 0;
    }

    // Lot
    private void refreshWorkOrderState(Long workOrderId) {
        // 1. 목표 수량 조회
        WorkOrder wo = productionMapper.getWorkOrderDetail(workOrderId);
        if (wo == null) return;

        // 2. 전체 생산량(Lot) 합계 계산
        List<Lot> lots = productionMapper.getWorkOrderLots(workOrderId);
        int totalProducedQty = lots.stream().mapToInt(Lot::getLot_qty).sum();

        // 3. 전체 불량(Defect) 합계 계산
        List<Defect> defects = productionMapper.getWorkOrderDefects(workOrderId);
        int totalDefectQty = defects.stream().mapToInt(Defect::getDefect_qty).sum();

        // 4. 실질 양품 수량 = 전체 생산량 - 전체 불량 수량
        int currentGoodQty = totalProducedQty - totalDefectQty;
        if (currentGoodQty < 0) currentGoodQty = 0; // 방어 코드

        // 5. 상태 판별
        int newStatus = 0; // 대기
        if (currentGoodQty >= wo.getTarget_qty()) {
            newStatus = 2; // 완료 (양품이 목표 달성 시)
        } else if (totalProducedQty > 0) {
            newStatus = 1; // 진행중 (생산 이력이 있으면)
        }

        // 6. DB 업데이트
        productionMapper.updateWorkOrderProgress(workOrderId, currentGoodQty, totalDefectQty, newStatus);
    }

    @Transactional
    public boolean addLot(Lot lot) {
        int result = productionMapper.addLot(lot);
        if (result > 0) {
            // 등록 후 상태 갱신 호출
            refreshWorkOrderState(lot.getWork_order_id());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean addDefect(Defect defect) {
        int result = productionMapper.addDefect(defect);
        if (result > 0) {
            // Lot ID를 통해 WorkOrder ID를 찾아서 상태 갱신 호출
            Lot lot = productionMapper.getLotById(defect.getLot_id());
            if (lot != null) {
                refreshWorkOrderState(lot.getWork_order_id());
            }
            return true;
        }
        return false;
    }

}