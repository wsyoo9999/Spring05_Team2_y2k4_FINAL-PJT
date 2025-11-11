package com.multi.y2k4.controller;

import com.multi.y2k4.vo.production.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    // 1. 작업지시서 목록 조회 (검색 포함)
    @GetMapping("/work_order")
    public List<WorkOrder> getWorkOrderList(
            @RequestParam(required = false) String order_status,
            @RequestParam(required = false) Long stock_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String due_date) {

        // 테스트용 더미 데이터
        List<WorkOrder> list = new ArrayList<>();

        WorkOrder wo1 = new WorkOrder();
        wo1.setWork_order_id(1001L);
        wo1.setStock_id(501L);
        wo1.setEmp_id(2001L);
        wo1.setStart_date(LocalDate.of(2025, 10, 1));
        wo1.setDue_date(LocalDate.of(2025, 11, 15));
        wo1.setTarget_qty(100);
        wo1.setGood_qty(70);
        wo1.setDefect_qty(10);
        wo1.setOrder_status("진행중");
        wo1.setRequest_date(LocalDateTime.of(2025, 9, 25, 9, 30));
        list.add(wo1);

        WorkOrder wo2 = new WorkOrder();
        wo2.setWork_order_id(1002L);
        wo2.setStock_id(502L);
        wo2.setEmp_id(2002L);
        wo2.setStart_date(LocalDate.of(2025, 9, 20));
        wo2.setDue_date(LocalDate.of(2025, 10, 30));
        wo2.setTarget_qty(50);
        wo2.setGood_qty(50);
        wo2.setDefect_qty(3);
        wo2.setOrder_status("완료");
        wo2.setRequest_date(LocalDateTime.of(2025, 9, 5, 14, 0));
        list.add(wo2);

        WorkOrder wo3 = new WorkOrder();
        wo3.setWork_order_id(1003L);
        wo3.setStock_id(503L);
        wo3.setEmp_id(2003L);
        wo3.setStart_date(LocalDate.of(2025, 11, 5));
        wo3.setDue_date(LocalDate.of(2025, 11, 20));
        wo3.setTarget_qty(80);
        wo3.setGood_qty(0);
        wo3.setDefect_qty(0);
        wo3.setOrder_status("대기");
        wo3.setRequest_date(LocalDateTime.of(2025, 10, 28, 16, 15));
        list.add(wo3);

        return list;
    }

    // 2. 작업지시서 상세 조회
    @GetMapping("/work_order/{work_order_id}")
    public WorkOrder getWorkOrderDetail(@PathVariable Long work_order_id) {
        // 테스트용 더미 데이터
        WorkOrder wo = new WorkOrder();
        wo.setWork_order_id(work_order_id);
        wo.setStock_id(501L);
        wo.setEmp_id(2001L);
        wo.setStart_date(LocalDate.of(2025, 10, 1));
        wo.setDue_date(LocalDate.of(2025, 11, 15));
        wo.setTarget_qty(100);
        wo.setGood_qty(70);
        wo.setDefect_qty(5);
        wo.setOrder_status("진행중");
        wo.setRequest_date(LocalDateTime.of(2025, 9, 25, 9, 30));

        return wo;
    }

    // 3. 작업지시서별 Lot 목록
    @GetMapping("/work_order/{work_order_id}/lots")
    public List<Lot> getWorkOrderLots(@PathVariable Long work_order_id) {
        List<Lot> list = new ArrayList<>();

        Lot lot1 = new Lot();
        lot1.setLot_id(1L);
        lot1.setWork_order_id(work_order_id);
        lot1.setStock_id(501L);
        lot1.setLot_qty(30);
        lot1.setLot_date(LocalDate.of(2025, 10, 5));
        list.add(lot1);

        Lot lot2 = new Lot();
        lot2.setLot_id(2L);
        lot2.setWork_order_id(work_order_id);
        lot2.setStock_id(501L);
        lot2.setLot_qty(40);
        lot2.setLot_date(LocalDate.of(2025, 10, 12));
        list.add(lot2);

        return list;
    }

    // 4. 작업지시서별 불량 내역
    @GetMapping("/work_order/{work_order_id}/defects")
    public List<Defect> getWorkOrderDefects(@PathVariable Long work_order_id) {
        List<Defect> list = new ArrayList<>();

        Defect d1 = new Defect();
        d1.setDefect_code(9001L);
        d1.setWork_order_id(work_order_id);
        d1.setLot_id(1L);
        d1.setStock_id(501L);
        d1.setDefect_qty(3);
        d1.setDefect_date(LocalDate.of(2025, 10, 10));
        list.add(d1);

        Defect d2 = new Defect();
        d2.setDefect_code(9002L);
        d2.setWork_order_id(work_order_id);
        d2.setLot_id(2L);
        d2.setStock_id(501L);
        d2.setDefect_qty(2);
        d2.setDefect_date(LocalDate.of(2025, 10, 15));
        list.add(d2);

        return list;
    }

    // 5. BOM 목록 조회 (검색 포함)
    @GetMapping("/bom")
    public List<BOM> getBOMList(@RequestParam(required = false) Long parent_stock_id) {
        List<BOM> list = new ArrayList<>();

        BOM bom1 = new BOM();
        bom1.setParent_stock_id(501L);
        bom1.setChild_stock_id(301L);
        bom1.setRequired_qty(10);
        list.add(bom1);

        BOM bom2 = new BOM();
        bom2.setParent_stock_id(501L);
        bom2.setChild_stock_id(302L);
        bom2.setRequired_qty(7);
        list.add(bom2);

        BOM bom3 = new BOM();
        bom3.setParent_stock_id(502L);
        bom3.setChild_stock_id(303L);
        bom3.setRequired_qty(8);
        list.add(bom3);

        return list;
    }

    @PostMapping("/work_order/add")
    public boolean addWorkOrder(
            @RequestParam Long stock_id,
            @RequestParam Long emp_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
            @RequestParam Integer target_qty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime request_date
    ) {
        // 팝업 폼에서 전송된 데이터 확인
        System.out.println("--- 작업지시서 신규 등록 ---");
        System.out.println("stock_id : " + stock_id);
        System.out.println("emp_id : " + emp_id);
        System.out.println("start_date : " + start_date);
        System.out.println("due_date : " + due_date);
        System.out.println("target_qty : " + target_qty);
        System.out.println("request_date : " + request_date);

        // (추후 DB 저장 로직 구현)

        return true; // addSale과 동일하게 true 반환
    }
}
