package com.multi.y2k4.controller;

import com.multi.y2k4.service.production.ProductionService; // Service 임포트
import com.multi.y2k4.vo.production.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService; // Mapper 대신 Service 사용

    // 1. 작업지시서 목록 조회
    @GetMapping("/work_order")
    public List<WorkOrder> getWorkOrderList(
            @RequestParam(required = false) String order_status,
            @RequestParam(required = false) String stock_name,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String due_date) {
        return productionService.getWorkOrderList(order_status, stock_name, start_date, due_date);
    }

    // 2. 작업지시서 상세 조회
    @GetMapping("/work_order/{work_order_id}")
    public WorkOrder getWorkOrderDetail(@PathVariable Long work_order_id) {
        return productionService.getWorkOrderDetail(work_order_id);
    }

    // 3. 작업지시서별 Lot 목록
    @GetMapping("/work_order/{work_order_id}/lots")
    public List<Lot> getWorkOrderLots(@PathVariable Long work_order_id) {
        return productionService.getWorkOrderLots(work_order_id);
    }

    // 4. 작업지시서별 불량 내역
    @GetMapping("/work_order/{work_order_id}/defects")
    public List<Defect> getWorkOrderDefects(@PathVariable Long work_order_id) {
        return productionService.getWorkOrderDefects(work_order_id);
    }

    // 5. BOM 목록 조회
    @GetMapping("/bom")
    public List<BOM> getBOMList(
            @RequestParam(required = false) Long parent_stock_id,
            @RequestParam(required = false) Long child_stock_id) {
        return productionService.getBOMList(parent_stock_id, child_stock_id);
    }

    // 6. BOM 상세 조회 (수정 팝업용)
    @GetMapping("/bom/{bom_id}")
    public ResponseEntity<BOM> getBOMById(@PathVariable Long bom_id) {
        BOM bom = productionService.getBOMById(bom_id);
        return (bom != null) ? ResponseEntity.ok(bom) : ResponseEntity.notFound().build();
    }

    // CUD

    @PostMapping("/work_order/add")
    public boolean addWorkOrder(
            @RequestParam Long stock_id,
            @RequestParam Long emp_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam Integer target_qty
    ) {
        WorkOrder newWorkOrder = new WorkOrder();
        newWorkOrder.setStock_id(stock_id);
        newWorkOrder.setEmp_id(emp_id);
        newWorkOrder.setStart_date(start_date);
        newWorkOrder.setTarget_qty(target_qty);
        newWorkOrder.setRequest_date(LocalDateTime.now());

        return productionService.addWorkOrder(newWorkOrder);
    }

    @DeleteMapping("/work_order/{work_order_id}")
    public boolean deleteWorkOrder(@PathVariable Long work_order_id) {
        return productionService.deleteWorkOrder(work_order_id);
    }

    @PostMapping("/bom/add")
    public boolean addBOM(
            @RequestParam Long parent_stock_id,
            @RequestParam Long child_stock_id,
            @RequestParam Integer required_qty
    ) {
        BOM newBOM = new BOM();
        newBOM.setParent_stock_id(parent_stock_id);
        newBOM.setChild_stock_id(child_stock_id);
        newBOM.setRequired_qty(required_qty);

        return productionService.addBOM(newBOM);
    }

    @PutMapping("/bom/{bom_id}")
    public boolean updateBOM(
            @PathVariable Long bom_id,
            @RequestBody BOM bom
    ) {
        if (!bom_id.equals(bom.getBom_id())) {
            return false;
        }
        return productionService.updateBOM(bom);
    }

    @DeleteMapping("/bom/{bom_id}")
    public boolean deleteBOM(@PathVariable Long bom_id) {
        return productionService.deleteBOM(bom_id);
    }

    @PostMapping("/lot/add")
    public boolean addLot(
            @RequestParam Long work_order_id,
            @RequestParam Long stock_id,
            @RequestParam Integer lot_qty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lot_date
    ) {
        Lot newLot = new Lot();
        newLot.setWork_order_id(work_order_id);
        newLot.setStock_id(stock_id);
        newLot.setLot_qty(lot_qty);
        newLot.setLot_date(lot_date);

        return productionService.addLot(newLot);
    }
}