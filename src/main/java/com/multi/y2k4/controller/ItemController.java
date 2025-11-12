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
        stock1.setStock_id(1);
        stock1.setStock_name("덩기덕");
        stockList.add(stock1);

        Stock stock2 = new Stock();
        stock2.setStock_id(2);
        stock2.setStock_name("쿵따");
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
        inbound1.setStock_id(1);
        inboundList.add(inbound1);

        Inbound inbound2 = new Inbound();
        inbound2.setStock_id(2);
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
        outbound1.setStock_id(1);
        outboundList.add(outbound1);

        Outbound outbound2 = new Outbound();
        outbound2.setStock_id(2);
        outboundList.add(outbound2);

        Outbound outbound3 = new Outbound();
        outboundList.add(outbound3);

        return outboundList;
    }

}
