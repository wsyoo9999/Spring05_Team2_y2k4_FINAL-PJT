package com.multi.y2k4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.production.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;

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
            @RequestParam(required = false) String parent_stock_name,
            @RequestParam(required = false) String child_stock_name) {
        return productionService.getBOMList(parent_stock_name, child_stock_name);
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
        try {
            // 1. 작업 지시서 객체 생성
            WorkOrder newWorkOrder = new WorkOrder();
            newWorkOrder.setStock_id(stock_id);
            newWorkOrder.setEmp_id(emp_id);
            newWorkOrder.setStart_date(start_date);
            newWorkOrder.setTarget_qty(target_qty);
            newWorkOrder.setRequest_date(LocalDateTime.now());
            // 결재 대기 중인 상태로 임시 저장하거나, 로직에 따라 상태값 설정 (예: "대기" 또는 특정 코드)
            newWorkOrder.setOrder_status("대기");
            // productionService.addWorkOrder(newWorkOrder);

            // 2. 결재 문서용 JSON 데이터 생성 (DocumentBodyBuilder에서 사용할 데이터)
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 2);   // 카테고리: 생산/제조
            payload.put("tb_id", 0);    // 테이블: 작업지시서
            payload.put("cd_id", 0);    // 동작: 추가 (Create)

            // DocumentBodyBuilder에서 꺼낼 키 이름("workOrder")과 객체를 담습니다.
            payload.put("workOrder", newWorkOrder);

            // 3. 결재 문서 객체 생성 및 정보 설정
            Documents doc = new Documents();
            doc.setTitle("[생산] 작업지시서 등록 요청 - " + start_date); // 문서 제목
            doc.setReq_id(emp_id);           // 기안자 ID (현재 로그인한 사용자 ID로 변경 필요 시 수정)
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0);                // 결재 상태: 0(대기)

            // JSON 변환 후 쿼리 스트링 저장
            String query = objectMapper.writeValueAsString(payload);
            doc.setQuery(query);

            // 4. 결재 문서 저장 (이때 DocumentBodyBuilder가 호출되어 본문 HTML이 생성됨)
            documentsService.addDocument(doc);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @DeleteMapping("/work_order/{work_order_id}")
    public boolean deleteWorkOrder(@PathVariable Long work_order_id) {
        try {
            // 1. 삭제할 대상 정보 조회
            WorkOrder targetWo = productionService.getWorkOrderDetail(work_order_id);

            if (targetWo == null) {
                System.out.println("삭제 대상 작업지시서가 존재하지 않습니다: " + work_order_id);
                return false;
            }

            // 2. 결재 문서용 JSON 데이터 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 2);   // 카테고리: 생산/제조
            payload.put("tb_id", 0);    // 테이블: 작업지시서
            payload.put("cd_id", 2);    // 동작: 삭제 (Delete)

            // DocumentBodyBuilder가 데이터를 읽을 수 있도록 "workOrder" 키에 객체 저장
            payload.put("workOrder", targetWo);

            // 3. 결재 문서 객체 생성 및 정보 설정
            Documents doc = new Documents();
            // 문서 제목 설정 (예: [생산] 작업지시서 삭제 요청 - 제품명)
            String title = "[생산] 작업지시서 삭제 요청 - " +
                    (targetWo.getStock_name() != null ? targetWo.getStock_name() : "ID:" + work_order_id);
            doc.setTitle(title);

            // 기안자 ID 설정 (삭제를 요청한 담당자 ID 사용, 필요 시 로그인 세션 ID로 변경 가능)
            doc.setReq_id(targetWo.getEmp_id());
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0); // 결재 상태: 0(대기)

            // JSON 변환 및 쿼리 저장
            String query = objectMapper.writeValueAsString(payload);
            doc.setQuery(query);

            // 4. 결재 문서 저장 (결재 요청 발송)
            documentsService.addDocument(doc);

            // 실제 삭제는 여기서 수행하지 않음 (결재 승인 시 수행)
            // productionService.deleteWorkOrder(work_order_id);

            return true;

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

    @PostMapping("/defect/add")
    public boolean addDefect(
            @RequestParam Long lot_id,
            @RequestParam Integer defect_code, // [추가] 불량 코드
            @RequestParam Integer defect_qty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate defect_date
    ) {
        Defect defect = new Defect();
        defect.setLot_id(lot_id);
        defect.setDefect_code(Long.valueOf(defect_code)); // VO 타입에 맞게 변환
        defect.setDefect_qty(defect_qty);
        defect.setDefect_date(defect_date);

        return productionService.addDefect(defect);
    }

    @DeleteMapping("/lot/{lot_id}")
    public boolean deleteLot(@PathVariable Long lot_id) {
        return productionService.deleteLot(lot_id);
    }
}