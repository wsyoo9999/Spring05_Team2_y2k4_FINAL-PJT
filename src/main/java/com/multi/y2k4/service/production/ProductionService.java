package com.multi.y2k4.service.production;

import com.multi.y2k4.mapper.tenant.production.ProductionMapper;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.vo.production.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionService {

    private final ProductionMapper productionMapper;
    private final StockService stockService;
    public List<BOM> getBOMListByParentId(Long parentStockId) {
        return productionMapper.getBOMListByParentId(parentStockId);
    }

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

    @Transactional
    public boolean addWorkOrder(WorkOrder workOrder) {
        return productionMapper.addWorkOrder(workOrder) > 0;
    }

    // [수정됨] 작업지시서 승인 확정 시 호출: 재고(acquired_qty) 반영 수행
    @Transactional
    public void confirmWorkOrderCreation(Long workOrderId) {
        WorkOrder workOrder = productionMapper.getWorkOrderDetail(workOrderId);
        if (workOrder == null) return;

        // 1. [완제품] acquired_qty 감소 (-) (생산 예정 수량 = 아직 없으므로 마이너스로 표기하거나 요구사항 반영)
        // 기존 1(증가) -> 2(감소)로 변경
        stockService.manageAcquiredAty(
                workOrder.getStock_id().intValue(),
                2,
                workOrder.getTarget_qty()
        );

        // 2. [원자재] acquired_qty 증가 (+) (자재 소요 예정)
        // 기존 2(감소) -> 1(증가)로 변경
        List<BOM> bomList = productionMapper.getBOMListByParentId(workOrder.getStock_id());

        if (bomList != null) {
            for (BOM bom : bomList) {
                int requiredAmount = bom.getRequired_qty() * workOrder.getTarget_qty();

                stockService.manageAcquiredAty(
                        bom.getChild_stock_id().intValue(),
                        1,
                        requiredAmount
                );
            }
        }
    }

    @Transactional
    public boolean deleteWorkOrder(Long work_order_id) {
        // 1. [자식의 자식] 불량 내역(Defect) 먼저 삭제
        productionMapper.deleteDefectsByWorkOrderId(work_order_id);

        // 2. [자식] 생산 실적(Lot) 삭제
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
        int oldStatus = 0;
        if ("진행중".equals(wo.getOrder_status())) oldStatus = 1;
        else if ("완료".equals(wo.getOrder_status())) oldStatus = 2;

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

        if (oldStatus != 2 && newStatus == 2) {
            // 1) 완제품 실제 재고(qty) 증가 : 최종 양품 수량만큼 입고
            stockService.manageStockQty(wo.getStock_id().intValue(), 1, currentGoodQty);

            // 2) 완제품 요청 수량(acquired_qty) 정리
            // 생성 시 (-) 였으므로, 완료 시 (+) 하여 0으로 만듦
            // 기존 2(감소) -> 1(증가)로 변경
            stockService.manageAcquiredAty(wo.getStock_id().intValue(), 1, wo.getTarget_qty());
        }

        // 6. DB 업데이트
        productionMapper.updateWorkOrderProgress(workOrderId, currentGoodQty, totalDefectQty, newStatus);
    }

    @Transactional
    public boolean addLot(Lot lot, Integer defectCode, Integer defectQty) {
        WorkOrder wo = productionMapper.getWorkOrderDetail(lot.getWork_order_id());
        if (wo == null) return false;
        if ("폐기".equals(wo.getOrder_status()) || "완료".equals(wo.getOrder_status())) {
            return false;
        }

        List<BOM> bomList = productionMapper.getBOMListByParentId(wo.getStock_id());

        if (bomList != null && !bomList.isEmpty()) {
            List<Integer> childStockIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();       // 실제 재고 차감용 (총 생산량 기준)
            List<Integer> acquiredQuantities = new ArrayList<>(); // 요청 수량 차감용 (양품 기준)

            // [수정] 불량 수량 null 체크 및 양품 수량 계산
            int currentDefectQty = (defectQty != null) ? defectQty : 0;
            int goodQty = lot.getLot_qty() - currentDefectQty;
            if (goodQty < 0) goodQty = 0; // 방어 코드

            for (BOM bom : bomList) {
                childStockIds.add(bom.getChild_stock_id().intValue());

                // 1. 실제 재고 차감 (qty): 불량품을 포함한 '총 생산량' 만큼 자재 소모
                int realAmount = bom.getRequired_qty() * lot.getLot_qty();
                quantities.add(realAmount);

                // 2. 요청 수량 차감 (acquired_qty): '양품 수량' 만큼만 예약 해제 (사용자 요청 반영)
                int acquiredAmount = bom.getRequired_qty() * goodQty;

                // manageStock type 2는 내부적으로 -value를 수행하므로 양수를 넘겨야 차감됨
                acquiredQuantities.add(acquiredAmount);
            }

            List<Integer> result = stockService.manageStock(childStockIds, quantities, acquiredQuantities, 2);

            if (result == null) {
                return false; // 재고 부족
            }
        }

        int result = productionMapper.addLot(lot);

        if (result > 0) {
            Long generatedLotId = lot.getLot_id();
            if (defectQty != null && defectQty > 0) {
                Defect defect = new Defect();
                defect.setLot_id(generatedLotId);
                defect.setWork_order_id(lot.getWork_order_id());
                defect.setStock_id(lot.getStock_id());
                defect.setDefect_code(defectCode != null ? defectCode.longValue() : 99L);
                defect.setDefect_qty(defectQty);
                defect.setDefect_date(lot.getLot_date());
                productionMapper.addDefect(defect);
            }
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

    @Transactional
    public boolean deleteLot(Long lot_id) {
        Lot targetLot = productionMapper.getLotById(lot_id);
        if (targetLot == null) {
            return false;
        }
        Long workOrderId = targetLot.getWork_order_id();

        // FK 제약조건 해결 위해 불량 먼저 삭제
        productionMapper.deleteDefectsByLotId(lot_id);
        int result = productionMapper.deleteLot(lot_id);

        if (result > 0) {
            refreshWorkOrderState(workOrderId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateWorkOrderStatus(Long workOrderId, int status) {
        int result = productionMapper.updateWorkOrderStatus(workOrderId, status);

        // 폐기(3) 시 롤백 로직
        if (result > 0 && status == 3) {
            WorkOrder wo = productionMapper.getWorkOrderDetail(workOrderId);
            if (wo != null) {

                // (1) 생산된 완제품(양품)을 실물 재고(qty)에 반영
                if (wo.getGood_qty() != null && wo.getGood_qty() > 0) {
                    stockService.manageStockQty(wo.getStock_id().intValue(), 1, wo.getGood_qty());
                }

                // (2) 완제품(Parent) 요청 수량 롤백
                // 승인 시 2(감소)했으므로, 폐기 시 1(증가)하여 원래대로 복구
                stockService.manageAcquiredAty(wo.getStock_id().intValue(), 1, wo.getTarget_qty());

                // (3) 원자재(Child) 요청 수량 롤백 (잔여 예약 해제)
                // 승인 시 1(증가)했으므로, 폐기 시 2(감소)하여 해제
                // 기준: (목표 - 양품 수량)
                int currentGoodQty = (wo.getGood_qty() != null) ? wo.getGood_qty() : 0;
                int remainingQty = wo.getTarget_qty() - currentGoodQty;

                if (remainingQty != 0) {
                    List<BOM> bomList = productionMapper.getBOMListByParentId(wo.getStock_id());
                    if (bomList != null) {
                        for (BOM bom : bomList) {
                            int releaseAmount = bom.getRequired_qty() * remainingQty;
                            stockService.manageAcquiredAty(bom.getChild_stock_id().intValue(), 2, releaseAmount);
                        }
                    }
                }
            }
        }
        return result > 0;
    }
}