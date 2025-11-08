package com.multi.y2k4.controller;

import com.multi.y2k4.vo.production.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/production")
public class ProductionController {

    // 1. 작업지시서 목록 조회 (검색 포함)
    @GetMapping("/work_order")
    public List<WorkOrder> getWorkOrderList(
            @RequestParam(required = false) String order_status,
            @RequestParam(required = false) Integer item_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String due_date) {

        // 테스트용 더미 데이터
        List<WorkOrder> list = new ArrayList<>();

        WorkOrder wo1 = new WorkOrder();
        wo1.setOrder_id(1001);
        wo1.setItem_id(101);
        wo1.setStart_date(LocalDate.of(2025, 10, 1));
        wo1.setDue_date(LocalDate.of(2025, 11, 15));
        wo1.setTarget_quantity(100);
        wo1.setGood_quantity(70);
        wo1.setDefect_quantity(10);
        wo1.setOrder_status("진행중");
        list.add(wo1);

        WorkOrder wo2 = new WorkOrder();
        wo2.setOrder_id(1002);
        wo2.setItem_id(102);
        wo2.setStart_date(LocalDate.of(2025, 9, 20));
        wo2.setDue_date(LocalDate.of(2025, 10, 30));
        wo2.setTarget_quantity(50);
        wo2.setGood_quantity(50);
        wo2.setDefect_quantity(3);
        wo2.setOrder_status("완료");
        list.add(wo2);

        WorkOrder wo3 = new WorkOrder();
        wo3.setOrder_id(1003);
        wo3.setItem_id(103);
        wo3.setStart_date(LocalDate.of(2025, 11, 5));
        wo3.setDue_date(LocalDate.of(2025, 11, 20));
        wo3.setTarget_quantity(80);
        wo3.setGood_quantity(0);
        wo3.setDefect_quantity(0);
        wo3.setOrder_status("대기");
        list.add(wo3);

        return list;
    }

    // 2. 작업지시서 상세 조회
    @GetMapping("/work_order/{order_id}")
    public WorkOrder getWorkOrderDetail(@PathVariable Integer order_id) {
        // 테스트용 더미 데이터
        WorkOrder wo = new WorkOrder();
        wo.setOrder_id(order_id);
        wo.setItem_id(101);
        wo.setStart_date(LocalDate.of(2025, 10, 1));
        wo.setDue_date(LocalDate.of(2025, 11, 15));
        wo.setTarget_quantity(100);
        wo.setGood_quantity(70);
        wo.setDefect_quantity(5);
        wo.setOrder_status("진행중");

        return wo;
    }

    // 3. 작업지시서별 Lot 목록
    @GetMapping("/work_order/{order_id}/lots")
    public List<Lot> getWorkOrderLots(@PathVariable Integer order_id) {
        List<Lot> list = new ArrayList<>();

        Lot lot1 = new Lot();
        lot1.setLot_id(1);
        lot1.setLot_number("LOT-2025-1001-001");
        lot1.setItem_id(101);
        lot1.setProduction_date(LocalDate.of(2025, 10, 5));
        lot1.setLot_quantity(30);
        lot1.setOrder_id(order_id);
        list.add(lot1);

        Lot lot2 = new Lot();
        lot2.setLot_id(2);
        lot2.setLot_number("LOT-2025-1001-002");
        lot2.setItem_id(101);
        lot2.setProduction_date(LocalDate.of(2025, 10, 12));
        lot2.setLot_quantity(40);
        lot2.setOrder_id(order_id);
        list.add(lot2);

        return list;
    }

    // 4. 작업지시서별 불량 내역
    @GetMapping("/work_order/{order_id}/defects")
    public List<Defect> getWorkOrderDefects(@PathVariable Integer order_id) {
        List<Defect> list = new ArrayList<>();

        Defect d1 = new Defect();
        d1.setDefect_id(1);
        d1.setDefect_name("표면 긁힘");
        d1.setDefect_quantity(3);
        d1.setDetected_date(LocalDate.of(2025, 10, 10));
        d1.setOrder_id(order_id);
        list.add(d1);

        Defect d2 = new Defect();
        d2.setDefect_id(2);
        d2.setDefect_name("치수 불량");
        d2.setDefect_quantity(2);
        d2.setDetected_date(LocalDate.of(2025, 10, 15));
        d2.setOrder_id(order_id);
        list.add(d2);

        return list;
    }

    // 5. BOM 목록 조회 (검색 포함)
    @GetMapping("/bom")
    public List<BOM> getBOMList(@RequestParam(required = false) Integer item_id) {
        List<BOM> list = new ArrayList<>();

        BOM bom1 = new BOM();
        bom1.setBom_id(1);
        bom1.setRaw_materials_code("RAW-001");
        bom1.setItem_id(101);
        bom1.setRequired_quantity(10);
        list.add(bom1);

        BOM bom2 = new BOM();
        bom2.setBom_id(2);
        bom2.setRaw_materials_code("RAW-002");
        bom2.setItem_id(101);
        bom2.setRequired_quantity(7);
        list.add(bom2);

        BOM bom3 = new BOM();
        bom3.setBom_id(3);
        bom3.setRaw_materials_code("RAW-003");
        bom3.setItem_id(102);
        bom3.setRequired_quantity(8);
        list.add(bom3);

        return list;
    }

    @PostMapping("/work_order/add")
    public boolean addWorkOrder(
            @RequestParam Integer item_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
            @RequestParam Integer target_quantity
    ) {
        // 팝업 폼에서 전송된 데이터 확인
        System.out.println("--- 작업지시서 신규 등록 ---");
        System.out.println("item_id : " + item_id);
        System.out.println("start_date : " + start_date);
        System.out.println("due_date : " + due_date);
        System.out.println("target_quantity : " + target_quantity);

        // (추후 DB 저장 로직 구현)

        return true; // addSale과 동일하게 true 반환
    }
}
