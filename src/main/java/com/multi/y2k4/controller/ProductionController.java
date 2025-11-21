package com.multi.y2k4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.production.*;
import com.multi.y2k4.vo.inventory.Stock;
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
    private final StockService stockService;
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
            // 1. 작업 지시서 객체 생성 및 DB 저장 (선저장)
            WorkOrder newWorkOrder = new WorkOrder();
            newWorkOrder.setStock_id(stock_id);
            newWorkOrder.setEmp_id(emp_id);
            newWorkOrder.setStart_date(start_date);
            newWorkOrder.setTarget_qty(target_qty);
            newWorkOrder.setRequest_date(LocalDateTime.now());

            // [핵심] 상태를 '99'(승인 대기)로 설정하여 임시 저장
            newWorkOrder.setOrder_status("99");

            // DB에 저장 (insert)
            productionService.addWorkOrder(newWorkOrder);
            String stockName = "제품명 미상";
            Stock stock = stockService.selectStockById(stock_id.intValue());
            if (stock != null) {
                stockName = stock.getStock_name();
            }

            // 2. 결재 문서용 JSON 데이터 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 2);   // 카테고리: 생산/제조
            payload.put("tb_id", 0);    // 테이블: 작업지시서
            payload.put("cd_id", 0);    // 동작: 추가

            // 생성된 PK를 문서에 저장 (나중에 승인 시 이 PK로 update함)
            payload.put("pk", newWorkOrder.getWork_order_id());

            // 상세 보기를 위해 객체 정보도 같이 넣어둠 (선택 사항)
            payload.put("workOrder", newWorkOrder);

            // 3. 결재 문서 객체 생성
            Documents doc = new Documents();
            doc.setTitle("작업지시서 등록 요청 - " + stockName);
            doc.setReq_id(emp_id);
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0); // 대기
            doc.setCat_id(2); // 생산/제조
            doc.setTb_id(0);  // 작업지시서
            doc.setCd_id(0);  // 추가

            String query = objectMapper.writeValueAsString(payload);
            doc.setQuery(query);

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
            // 1. 대상 정보 조회
            WorkOrder targetWo = productionService.getWorkOrderDetail(work_order_id);
            if (targetWo == null) return false;

            // 2. 상태 확인
            String statusStr = targetWo.getOrder_status(); // "대기", "진행중", "완료" ...
            int cd_id;
            String titleSuffix;
            Map<String, Object> payload = new HashMap<>();

            if ("완료".equals(statusStr) || "폐기".equals(statusStr)) {
                // [완료/폐기] 상태는 삭제/폐기 불가
                System.out.println("완료되거나 이미 폐기된 작업지시서는 삭제할 수 없습니다.");
                return false;
            } else if ("진행중".equals(statusStr)) {
                // [진행중] -> 폐기 요청 (수정 문서 생성)
                cd_id = 1; // 수정(Update)
                titleSuffix = "폐기 요청";
                payload.put("newStatus", 3); // 3: 폐기 상태 코드
            } else {
                // [대기] (또는 승인대기) -> 삭제 요청 (삭제 문서 생성)
                cd_id = 2; // 삭제(Delete)
                titleSuffix = "삭제 요청";
            }

            // 3. Payload 구성
            payload.put("cat_id", 2);
            payload.put("tb_id", 0);
            payload.put("cd_id", cd_id);
            payload.put("pk", work_order_id);
            payload.put("workOrder", targetWo); // 상세 정보 표시용

            // 4. 문서 생성
            Documents doc = new Documents();
            String stockName = targetWo.getStock_name() != null ? targetWo.getStock_name() : "ID:" + work_order_id;
            doc.setTitle("작업지시서 " + titleSuffix + " - " + stockName);

            doc.setReq_id(targetWo.getEmp_id());
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0); // 대기
            doc.setCat_id(2);
            doc.setTb_id(0);
            doc.setCd_id(cd_id); // 1(폐기) or 2(삭제)

            doc.setQuery(objectMapper.writeValueAsString(payload));

            documentsService.addDocument(doc);

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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lot_date,
            @RequestParam(required = false, defaultValue = "0") Integer defect_code,
            @RequestParam(required = false, defaultValue = "0") Integer defect_qty
    ) {
        Lot newLot = new Lot();
        newLot.setWork_order_id(work_order_id);
        newLot.setStock_id(stock_id);
        newLot.setLot_qty(lot_qty);
        newLot.setLot_date(lot_date);

        return productionService.addLot(newLot, defect_code, defect_qty);
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