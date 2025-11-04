package com.multi.y2k4.controller;


import com.multi.y2k4.vo.item.Inbound;
import com.multi.y2k4.vo.item.Item;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class ItemController {

    @GetMapping("/item")    // 물품 목록 출력
    public List<Item> itemsList(@RequestParam(required = false) Integer item_id,
                           @RequestParam(required = false) String item_name,
                           @RequestParam(required = false) Integer item_qty,
                           @RequestParam(required = false) Integer unit_price,
                           @RequestParam(required = false) Integer total_price,
                           @RequestParam(required = false) Integer item_status,
                           @RequestParam(required = false) String storage_location,
                           Model model) {

        // 테스트를 위한 임의의 데이터 생성
        List<Item> itemList = new ArrayList<>();

        Item item1 = new Item();
        item1.setItem_id(1001);
        item1.setItem_name("고속절단기");
        item1.setItem_qty(5);
        item1.setUnit_price(120000);
        item1.getTotal_price();
        item1.setItem_status(0); // 정상
        item1.setStorage_location("A-01-03"); // 창고 구역 코드
        itemList.add(item1);

//        System.out.println("총금액 테스트: " + item1.getTotal_price());

        Item item2 = new Item();
        item2.setItem_id(1002);
        item2.setItem_name("산업용 드릴");
        item2.setItem_qty(2);
        item2.setUnit_price(350000);
        item2.getTotal_price();
        item2.setItem_status(1); // 불량
        item2.setStorage_location("B-02-05");
        itemList.add(item2);

        return itemList;
    }


    @GetMapping("/inbound")    // 입고 목록 출력
    public List<Inbound> inboundList(
            @RequestParam(required = false) Integer inbound_id,
            @RequestParam(required = false) Integer item_id,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) Integer inbound_qty,
            @RequestParam(required = false) Integer unit_price,
            @RequestParam(required = false) Integer total_price,
            @RequestParam(required = false) String expand_date,
            Model model) {

        // 테스트용 임의 데이터 생성
        List<Inbound> inboundList = new ArrayList<>();

        Inbound inbound1 = new Inbound();
        inbound1.setInbound_id(2001);
        inbound1.setItem_id(1001);
        inbound1.setInbound_date(LocalDateTime.now().minusDays(2));  // 2일 전 입고
        inbound1.setItem_name("고속절단기");
        inbound1.setInbound_qty(5);
        inbound1.setUnit_price(120000);
        inbound1.getTotal_price();
        inbound1.setSupplier("삼성상사");
        inbound1.setExpand_date(LocalDate.now().plusMonths(6));       // 소비기한 6개월 후
        inboundList.add(inbound1);

        Inbound inbound2 = new Inbound();
        inbound2.setInbound_id(2002);
        inbound2.setItem_id(1002);
        inbound2.setInbound_date(LocalDateTime.now().minusDays(1));  // 1일 전 입고
        inbound2.setItem_name("산업용 드릴");
        inbound2.setInbound_qty(2);
        inbound2.setUnit_price(350000);
        inbound2.getTotal_price();
        inbound2.setSupplier("LG트레이딩");
        inbound2.setExpand_date(LocalDate.now().plusMonths(12));      // 소비기한 12개월 후
        inboundList.add(inbound2);

        return inboundList;
    }

}
