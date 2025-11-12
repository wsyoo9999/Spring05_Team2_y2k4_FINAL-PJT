package com.multi.y2k4.controller;

import com.multi.y2k4.mapper.tenant.production.ProductionMapper; // 1. 매퍼 임포트
import com.multi.y2k4.vo.production.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
// 2. ArrayList 임포트 제거 (더 이상 필요 없음)

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    // 3. Mapper 주입을 위한 final 필드 및 생성자 추가
    private final ProductionMapper productionMapper;

    public ProductionController(ProductionMapper productionMapper) {
        this.productionMapper = productionMapper;
    }

    // 1. 작업지시서 목록 조회 (검색 포함)
    @GetMapping("/work_order")
    public List<WorkOrder> getWorkOrderList(
            @RequestParam(required = false) String order_status,
            @RequestParam(required = false) Long stock_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String due_date) {

        // 4. 더미 데이터 코드 전부 삭제
        // 5. 매퍼 호출로 변경
        return productionMapper.getWorkOrderList(order_status, stock_id, start_date, due_date);
    }

    // 2. 작업지시서 상세 조회
    @GetMapping("/work_order/{work_order_id}")
    public WorkOrder getWorkOrderDetail(@PathVariable Long work_order_id) {
        // 4. 더미 데이터 코드 전부 삭제
        // 5. 매퍼 호출로 변경
        return productionMapper.getWorkOrderDetail(work_order_id);
    }

    // 3. 작업지시서별 Lot 목록
    @GetMapping("/work_order/{work_order_id}/lots")
    public List<Lot> getWorkOrderLots(@PathVariable Long work_order_id) {
        // 4. 더미 데이터 코드 전부 삭제
        // 5. 매퍼 호출로 변경
        return productionMapper.getWorkOrderLots(work_order_id);
    }

    // 4. 작업지시서별 불량 내역
    @GetMapping("/work_order/{work_order_id}/defects")
    public List<Defect> getWorkOrderDefects(@PathVariable Long work_order_id) {
        // 4. 더미 데이터 코드 전부 삭제
        // 5. 매퍼 호출로 변경
        return productionMapper.getWorkOrderDefects(work_order_id);
    }

    // 5. BOM 목록 조회 (검색 포함)
    @GetMapping("/bom")
    public List<BOM> getBOMList(
            @RequestParam(required = false) Long parent_stock_id,
            @RequestParam(required = false) Long child_stock_id) { // 1. child_stock_id 파라미터 추가

        // 2. 매퍼 호출 시 두 파라미터 모두 전달
        return productionMapper.getBOMList(parent_stock_id, child_stock_id);
    }
    @GetMapping("/bom/{bom_id}")
    public ResponseEntity<BOM> getBOMById(@PathVariable Long bom_id) {
        BOM bom = productionMapper.getBOMById(bom_id);
        if (bom != null) {
            return ResponseEntity.ok(bom);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/work_order/add")
    public boolean addWorkOrder(
            @RequestParam Long stock_id,
            @RequestParam Long emp_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            // @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date, // 1. 완료일 파라미터 제거
            @RequestParam Integer target_qty
            // @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime request_date // 2. 요청일시 파라미터 제거
    ) {
        // 팝업에서 받은 파라미터들을 WorkOrder VO 객체로 조립
        WorkOrder newWorkOrder = new WorkOrder();
        newWorkOrder.setStock_id(stock_id);
        newWorkOrder.setEmp_id(emp_id);
        newWorkOrder.setStart_date(start_date);
        // newWorkOrder.setDue_date(due_date); // 3. setDue_date 호출 제거 (VO에서 null로 유지됨)
        newWorkOrder.setTarget_qty(target_qty);
        newWorkOrder.setRequest_date(LocalDateTime.now()); // 4. 서버의 현재 시간으로 자동 설정

        // 매퍼를 호출하여 DB에 삽입
        try {
            int result = productionMapper.addWorkOrder(newWorkOrder);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @PostMapping("/bom/add")
    public boolean addBOM(
            @RequestParam Long parent_stock_id,
            @RequestParam Long child_stock_id,
            @RequestParam Integer required_qty
    ) {
        // 팝업에서 받은 파라미터들을 BOM VO 객체로 조립
        BOM newBOM = new BOM();
        newBOM.setParent_stock_id(parent_stock_id);
        newBOM.setChild_stock_id(child_stock_id);
        newBOM.setRequired_qty(required_qty);

        try {
            int result = productionMapper.addBOM(newBOM);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @PutMapping("/bom/{bom_id}")
    public boolean updateBOM(
            @PathVariable Long bom_id,
            @RequestBody BOM bom // 폼에서 JSON으로 전송된 데이터를 받음
    ) {
        // URL의 ID와 객체의 ID가 일치하는지 확인
        if (!bom_id.equals(bom.getBom_id())) {
            return false; // ID 불일치
        }

        try {
            int result = productionMapper.updateBOM(bom);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @DeleteMapping("/bom/{bom_id}")
    public boolean deleteBOM(@PathVariable Long bom_id) {
        try {
            int result = productionMapper.deleteBOM(bom_id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @DeleteMapping("/work_order/{work_order_id}")
    public boolean deleteWorkOrder(@PathVariable Long work_order_id) {
        try {
            int result = productionMapper.deleteWorkOrder(work_order_id);
            return result > 0; // 1줄 이상 삭제되었으면 true 반환
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 에러 출력
            return false; // 실패 시 false 반환
        }
    }
}
