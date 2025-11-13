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

    public List<WorkOrder> getWorkOrderList(String order_status, Long stock_id, String start_date, String due_date) {
        return productionMapper.getWorkOrderList(order_status, stock_id, start_date, due_date);
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
        return productionMapper.deleteWorkOrder(work_order_id) > 0;
    }

    // --- 자재 명세서 (BOM) ---

    public List<BOM> getBOMList(Long parent_stock_id, Long child_stock_id) {
        return productionMapper.getBOMList(parent_stock_id, child_stock_id);
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
}