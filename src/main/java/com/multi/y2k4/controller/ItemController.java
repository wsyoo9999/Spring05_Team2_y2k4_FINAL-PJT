package com.multi.y2k4.controller;


import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Outbound;
import com.multi.y2k4.vo.inventory.Stock;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class ItemController {

    @GetMapping("/stock")    // 물품 목록 출력
    public List<Stock> itemsList(@RequestParam(required = false) Integer item_id,
                                 @RequestParam(required = false) String item_name,
                                 @RequestParam(required = false) Integer item_qty,
                                 @RequestParam(required = false) Integer unit_price,
                                 @RequestParam(required = false) Integer total_price,
                                 @RequestParam(required = false) Integer item_status,
                                 @RequestParam(required = false) String storage_location,
                                 Model model) {

        // 테스트를 위한 임의의 데이터 생성
        List<Stock> stockList = new ArrayList<>();

        Stock stock1 = new Stock();
        stock1.setStock_id(1001);
        stock1.setStock_name("고속절단기");
        stock1.setStock_qty(5);
        stock1.setUnit_price(120000);
        stock1.setItem_status(0); // 정상
        stock1.setStorage_location("A-01-03"); // 창고 구역 코드
        stock1.setExpiration_date("6개월");
        stock1.setGubun(1);
        stockList.add(stock1);


//        System.out.println("총금액 테스트: " + item1.getTotal_price());

        Stock stock2 = new Stock();
        stock2.setStock_id(1002);
        stock2.setStock_name("산업용 드릴");
        stock2.setStock_qty(2);
        stock2.setUnit_price(350000);
        stock2.setItem_status(1); // 불량
        stock2.setStorage_location("B-02-05");
        stock2.setExpiration_date("-");
        stock2.setGubun(0);
        stockList.add(stock2);

        return stockList;
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
        inbound1.setInbound_order(1);
        inbound1.setStock_id(1001);
        inbound1.setInbound_date(LocalDateTime.now().minusDays(2));  // 2일 전 입고
        inbound1.setStock_name("고속절단기");
        inbound1.setInbound_qty(5);
        inbound1.setUnit_price(120000);
        inbound1.getTotal_price();
        inbound1.setSupplier("삼성상사");
        inbound1.setManager("홍길동");
        inbound1.setRemark("비고비고");
        inboundList.add(inbound1);

        Inbound inbound2 = new Inbound();
        inbound2.setInbound_order(2);
        inbound2.setStock_id(1002);
        inbound2.setInbound_date(LocalDateTime.now().minusDays(1));  // 1일 전 입고
        inbound2.setStock_name("산업용 드릴");
        inbound2.setInbound_qty(2);
        inbound2.setUnit_price(350000);
        inbound2.getTotal_price();
        inbound2.setSupplier("LG트레이딩");
        inbound2.setManager("동길홍");
        inbound2.setRemark("일이삼사오육칠팔구십일이삼사오육칠팔구십");
        inboundList.add(inbound2);

        return inboundList;
    }

    @GetMapping("/outbound")    // 출고 목록 출력
    public List<Outbound> outboundList(
            @RequestParam(required = false) Integer outbound_id,
            @RequestParam(required = false) Integer item_id,
            @RequestParam(required = false) String outbound_location,
            @RequestParam(required = false) Integer outbound_qty,
            Model model) {

        // 테스트용 임의 데이터 생성
        List<Outbound> outboundList = new ArrayList<>();

        Outbound outbound1 = new Outbound();
        outbound1.setOutbound_id(2001);
        outbound1.setStock_id(1001);
        outbound1.setOutbound_date(LocalDateTime.now().minusDays(1)); // 1일 전 출고
        outbound1.setOutbound_qty(3);
        outbound1.setOutbound_location("서울창고");
        outbound1.setManager("길홍동");
        outbound1.setRemark("-");
        outboundList.add(outbound1);

        Outbound outbound2 = new Outbound();
        outbound2.setOutbound_id(2002);
        outbound2.setStock_id(1002);
        outbound2.setOutbound_date(LocalDateTime.now().minusDays(2)); // 2일 전 출고
        outbound2.setOutbound_qty(5);
        outbound2.setOutbound_location("부산물류센터");
        outbound2.setManager("홍동길");
        outbound2.setRemark("");
        outboundList.add(outbound2);

        Outbound outbound3 = new Outbound();
        outbound3.setOutbound_id(2003);
        outbound3.setStock_id(1003);
        outbound3.setOutbound_date(LocalDateTime.now()); // 오늘 출고
        outbound3.setOutbound_qty(1);
        outbound3.setOutbound_location("대전지점");
        outbound3.setManager("길동홍");
        outbound3.setRemark("");
        outboundList.add(outbound3);

        return outboundList;
    }

}
