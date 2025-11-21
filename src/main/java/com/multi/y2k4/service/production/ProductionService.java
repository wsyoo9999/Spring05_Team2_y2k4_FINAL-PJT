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

    // [추가] 작업지시서 승인 확정 시 호출: 재고(acquired_qty) 반영 수행
    @Transactional
    public void confirmWorkOrderCreation(Long workOrderId) {
        WorkOrder workOrder = productionMapper.getWorkOrderDetail(workOrderId);
        if (workOrder == null) return;

        // 1. [완제품] acquired_qty 증가 (생산 예정 수량 확보)
        stockService.manageAcquiredAty(
                workOrder.getStock_id().intValue(),
                1,
                workOrder.getTarget_qty()
        );

        // 2. [원자재] acquired_qty 감소 (자재 예약 차감)
        List<BOM> bomList = productionMapper.getBOMListByParentId(workOrder.getStock_id());

        if (bomList != null) {
            for (BOM bom : bomList) {
                int requiredAmount = bom.getRequired_qty() * workOrder.getTarget_qty();

                stockService.manageAcquiredAty(
                        bom.getChild_stock_id().intValue(),
                        2,
                        requiredAmount
                );
            }
        }
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

        // [추가] 상태가 '완료'로 전환될 때 재고 및 요청 수량 일괄 처리
        if (oldStatus != 2 && newStatus == 2) {
            // 1) 완제품 실제 재고(qty) 증가 : 최종 양품 수량만큼 입고
            stockService.manageStockQty(wo.getStock_id().intValue(), 1, currentGoodQty);

            // 2) 완제품 요청 수량(acquired_qty) 차감 : 입고 예정(목표 수량) 해제
            stockService.manageAcquiredAty(wo.getStock_id().intValue(), 2, wo.getTarget_qty());
        }

        // 6. DB 업데이트
        productionMapper.updateWorkOrderProgress(workOrderId, currentGoodQty, totalDefectQty, newStatus);
    }

    @Transactional
    public boolean addLot(Lot lot, Integer defectCode, Integer defectQty) {
        // 1. 작업지시서 정보 조회 (완제품 ID 확인용)
        WorkOrder wo = productionMapper.getWorkOrderDetail(lot.getWork_order_id());
        if (wo == null) return false;

        // 2. BOM 조회 (필요한 자재 목록)
        List<BOM> bomList = productionMapper.getBOMListByParentId(wo.getStock_id());

        // 3. 자재 재고 체크 및 차감
        if (bomList != null && !bomList.isEmpty()) {
            List<Integer> childStockIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

            // acquired_qty (확보 수량) 변동 없음 처리를 위한 리스트
            // 생산 소모 시에는 실제 수량만 줄이고, acquired_qty는 건드리지 않기 위해 0으로 채움
            List<Integer> acquiredQuantities = new ArrayList<>();

            for (BOM bom : bomList) {
                childStockIds.add(bom.getChild_stock_id().intValue());

                // 소요량 = BOM필요수량 * 생산수량
                int requiredAmount = bom.getRequired_qty() * lot.getLot_qty();
                quantities.add(requiredAmount);

                // [추가] 해당 자재에 대해 acquired_qty는 0만큼 차감
                acquiredQuantities.add(-requiredAmount);
            }

            List<Integer> result = stockService.manageStock(childStockIds, quantities, acquiredQuantities, 2);

            if (result == null) {
                System.out.println("🚨 Lot 등록 실패: 원자재 재고 부족");
                return false; // 재고 부족으로 등록 중단
            }
        }

        // 4. Lot 등록 (실적 저장)
        int result = productionMapper.addLot(lot);

        if (result > 0) {
            Long generatedLotId = lot.getLot_id(); // 생성된 PK 가져오기

            // [추가] 불량이 있는 경우 Defect 테이블에 등록
            if (defectQty != null && defectQty > 0) {
                Defect defect = new Defect();
                defect.setLot_id(generatedLotId);
                defect.setWork_order_id(lot.getWork_order_id()); // 필요하다면 VO에 따라 설정
                defect.setStock_id(lot.getStock_id());           // 필요하다면 VO에 따라 설정

                // defectCode가 0이거나 없으면 '기타(99)' 등으로 처리하거나 그대로 저장
                defect.setDefect_code(defectCode != null ? defectCode.longValue() : 99L);
                defect.setDefect_qty(defectQty);
                defect.setDefect_date(lot.getLot_date()); // Lot 날짜와 동일하게 설정

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
        // 1. 삭제할 Lot 정보 조회 (삭제 후 상태 갱신을 위해 work_order_id가 필요함)
        Lot targetLot = productionMapper.getLotById(lot_id);
        if (targetLot == null) {
            return false; // 존재하지 않는 Lot
        }
        Long workOrderId = targetLot.getWork_order_id();

        // 2. 해당 Lot에 연결된 불량 내역(Defect) 먼저 삭제 (FK 제약조건 해결)
        productionMapper.deleteDefectsByLotId(lot_id);

        // 3. Lot 삭제
        int result = productionMapper.deleteLot(lot_id);

        // 4. 작업지시서 상태(수량, 진행률) 재계산 및 갱신
        if (result > 0) {
            refreshWorkOrderState(workOrderId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateWorkOrderStatus(Long workOrderId, int status) {
        return productionMapper.updateWorkOrderStatus(workOrderId, status) > 0;
    }

}