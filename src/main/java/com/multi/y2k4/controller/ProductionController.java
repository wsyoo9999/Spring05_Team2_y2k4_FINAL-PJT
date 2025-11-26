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
import jakarta.servlet.http.HttpSession;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.vo.hr.Employee;

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
    private final EmployeeService employeeService;

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
            @RequestParam Long emp_id, // 폼에서 선택한 담당자 (결재자가 됨)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam Integer target_qty,
            HttpSession session // 세션 추가
    ) {
        try {
            // 0. 현재 로그인한 사용자(기안자) 확인
            Integer currentEmpId = (Integer) session.getAttribute("emp_id");
            if (currentEmpId == null) {
                System.out.println("로그인 정보가 없습니다.");
                return false;
            }

            // 1. 작업 지시서 객체 생성 및 DB 저장 (선저장)
            WorkOrder newWorkOrder = new WorkOrder();
            newWorkOrder.setStock_id(stock_id);
            newWorkOrder.setEmp_id(emp_id); // 작업지시서상의 담당자는 선택된 사람으로 유지
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
                newWorkOrder.setStock_name(stockName); // VO에 이름 세팅
            }

            String empName = "담당자 미상";
            Employee emp = employeeService.getEmployeeDetail(emp_id.intValue());
            if (emp != null) {
                empName = emp.getEmp_name();
                newWorkOrder.setEmp_name(empName); // VO에 이름 세팅
            }

            // DB 저장
            productionService.addWorkOrder(newWorkOrder);

            // 2. 결재 문서용 JSON 데이터 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("cat_id", 2);
            payload.put("tb_id", 0);
            payload.put("cd_id", 0);
            payload.put("pk", newWorkOrder.getWork_order_id());

            // 이름 정보가 포함된 객체를 담음
            payload.put("workOrder", newWorkOrder);

            // 3. 결재 문서 객체 생성
            Documents doc = new Documents();
            doc.setTitle("작업지시서 등록 요청 - " + stockName);
            doc.setReq_id(Long.valueOf(currentEmpId));
            doc.setAppr_id(emp_id);
            doc.setReq_date(LocalDate.now());
            doc.setStatus(0);
            doc.setCat_id(2);
            doc.setTb_id(0);
            doc.setCd_id(0);

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
    public boolean deleteWorkOrder(@PathVariable Long work_order_id, HttpSession session) {
        try {
            // 0. 현재 로그인한 사용자(기안자) 확인
            Integer currentEmpId = (Integer) session.getAttribute("emp_id");
            if (currentEmpId == null) {
                return false;
            }

            // 1. 대상 정보 조회
            WorkOrder targetWo = productionService.getWorkOrderDetail(work_order_id);
            if (targetWo == null) return false;

            String statusStr = targetWo.getOrder_status();
            if ("승인대기".equals(statusStr)) {
                System.out.println("이미 결재 진행 중인 건입니다.");
                return false;
            }
            if ("완료".equals(statusStr) || "폐기".equals(statusStr)) {
                return false;
            }
            int cd_id;
            String titleSuffix;
            int originalStatusCode=0;
            Map<String, Object> payload = new HashMap<>();

            // 진행중인 작업 폐기 요청 시 상세 정보(Lot, Defect) 조회하여 추가
            if ("진행중".equals(statusStr)) {
                cd_id = 1; // 수정(Update) -> 폐기 요청 처리
                titleSuffix = "폐기 요청";
                payload.put("newStatus", 3); // 3: 폐기 상태 코드

                // 1) Lot 목록 조회
                List<Lot> lots = productionService.getWorkOrderLots(work_order_id);
                payload.put("lots", lots);

                // 2) 불량 내역 조회
                List<Defect> defects = productionService.getWorkOrderDefects(work_order_id);
                payload.put("defects", defects);

            } else if ("대기".equals(statusStr) || "승인대기".equals(statusStr)) { // 단순 삭제
                cd_id = 2; // 삭제(Delete)
                titleSuffix = "삭제 요청";
            } else {
                return false; // 완료/폐기 상태는 삭제 불가
            }

            // 3. Payload 구성
            payload.put("originalStatus", originalStatusCode);
            payload.put("cat_id", 2);
            payload.put("tb_id", 0);
            payload.put("cd_id", cd_id);
            payload.put("pk", work_order_id);

            // 이름 정보(stock_name, emp_name)가 포함된 최신 targetWo를 담아야 함
            payload.put("workOrder", targetWo);

            // 4. 문서 생성
            Documents doc = new Documents();
            String stockName = targetWo.getStock_name() != null ? targetWo.getStock_name() : "ID:" + work_order_id;
            doc.setTitle("작업지시서 " + titleSuffix + " - " + stockName);

            // 기안자: 로그인한 본인
            doc.setReq_id(Long.valueOf(currentEmpId));
            // 결재자: 작업지시서 담당자
            doc.setAppr_id(targetWo.getEmp_id());

            doc.setReq_date(LocalDate.now());
            doc.setStatus(0); // 대기
            doc.setCat_id(2);
            doc.setTb_id(0);
            doc.setCd_id(cd_id);

            doc.setQuery(objectMapper.writeValueAsString(payload));

            documentsService.addDocument(doc);

            productionService.updateWorkOrderStatus(work_order_id, 99);

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

    @GetMapping("/bom/by-parent/{parentStockId}")
    public List<BOM> getBOMListByParentId(@PathVariable Long parentStockId) {
        return productionService.getBOMListByParentId(parentStockId);
    }
}