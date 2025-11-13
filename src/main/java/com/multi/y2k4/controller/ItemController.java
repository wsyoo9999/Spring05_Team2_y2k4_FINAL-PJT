package com.multi.y2k4.controller;

import com.multi.y2k4.service.inventory.InboundService;
import com.multi.y2k4.service.inventory.OutboundService;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.vo.inventory.Inbound;
import com.multi.y2k4.vo.inventory.Outbound;
import com.multi.y2k4.vo.inventory.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class ItemController {
    private final StockService stockService;
    private final InboundService inboundService;
    private final OutboundService outboundService;

    // =============================================재고=============================================
    @GetMapping("/stock")
    public List<Stock> list(@RequestParam(required = false) String  stock_name,
                            @RequestParam(required = false) Integer qty,
                            @RequestParam(required = false) Integer unit_price,
                            @RequestParam(required = false) Integer  location,
                            @RequestParam(required = false) Integer type,
                            Model model) {
        // 서비스에 동일 시그니처가 있다고 가정
        System.out.println(stockService.list(stock_name, qty, unit_price, location,type));
        return stockService.list(stock_name, qty, unit_price, location, type);
    }

    // 재고 목록 추가
    @PostMapping("/stock/add")
    @Transactional
    public boolean addStock(@RequestParam String  stock_name,
                            @RequestParam Integer qty,
                            @RequestParam Integer unit_price,
                            @RequestParam Integer  location,
                            @RequestParam Integer type) {

        Stock stock = new Stock();
        stock.setStock_name(stock_name);
        stock.setQty(qty);
        stock.setUnit_price(unit_price);
        stock.setLocation(location);
        stock.setType(type);

        // addStock이 int(반영 행 수) 를 리턴한다고 가정
        int affected = stockService.addStock(stock);
        return affected == 1;
    }

    @PutMapping(value = "/stock/{stock_id}", consumes = "application/json")
    public ResponseEntity<Void> update(@PathVariable("stock_id") Integer stockId,
                                       @RequestBody Stock body) {
        body.setStock_id(stockId);              // PK는 경로로 고정
        int updated = stockService.updateStock(body); // Mapper: UPDATE stock SET ... WHERE stock_id=?
        return (updated == 1) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }





    // =============================================입고=============================================

    // 입고 목록 조회
    @GetMapping("/inbound")
    public List<Inbound> inboundList() {
        List<Inbound> inboundList = new ArrayList<>();

        Inbound inbound1 = new Inbound();
        inbound1.setInbound_id(1);
        inbound1.setStock_id(1);
        inbound1.setInbound_date(LocalDateTime.now().minusDays(2));
        inbound1.setInbound_qty(5);
        inbound1.setUnit_price(120000);
        inbound1.setAc_id(201);
        inbound1.setAc_name("삼성상사");
        inbound1.setEmp_id(101);
        inbound1.setEmp_name("홍길동");
        inbound1.setRemark("긴급 입고");
        inboundList.add(inbound1);

        Inbound inbound2 = new Inbound();
        inbound2.setInbound_id(2);
        inbound2.setStock_id(2);
        inbound2.setInbound_date(LocalDateTime.now().minusDays(1));
        inbound2.setInbound_qty(2);
        inbound2.setUnit_price(350000);
        inbound2.setAc_id(202);
        inbound2.setAc_name("LG트레이딩");
        inbound2.setEmp_id(102);
        inbound2.setEmp_name("김철수");
        inbound2.setRemark("정기 입고");
        inboundList.add(inbound2);

        Inbound inbound3 = new Inbound();
        inbound3.setInbound_id(3);
        inbound3.setStock_id(3);
        inbound3.setInbound_date(LocalDateTime.now());
        inbound3.setInbound_qty(10);
        inbound3.setUnit_price(80000);
        inbound3.setAc_id(203);
        inbound3.setAc_name("한국공구");
        inbound3.setEmp_id(103);
        inbound3.setEmp_name("이영희");
        inbound3.setRemark("추가 발주");
        inboundList.add(inbound3);

        return inboundList;
    }

    // 입고 단건 조회 (추가!)
    @GetMapping("/inbound/{inbound_id}")
    public Inbound getInboundById(@PathVariable int inbound_id) {
        Map<Integer, Inbound> inboundMap = new HashMap<>();

        Inbound inbound1 = new Inbound();
        inbound1.setInbound_id(1);
        inbound1.setStock_id(1);
        inbound1.setInbound_date(LocalDateTime.now().minusDays(2));
        inbound1.setInbound_qty(5);
        inbound1.setUnit_price(120000);
        inbound1.setAc_id(201);
        inbound1.setAc_name("삼성상사");
        inbound1.setEmp_id(101);
        inbound1.setEmp_name("홍길동");
        inbound1.setRemark("긴급 입고");
        inboundMap.put(1, inbound1);

        Inbound inbound2 = new Inbound();
        inbound2.setInbound_id(2);
        inbound2.setStock_id(2);
        inbound2.setInbound_date(LocalDateTime.now().minusDays(1));
        inbound2.setInbound_qty(2);
        inbound2.setUnit_price(350000);
        inbound2.setAc_id(202);
        inbound2.setAc_name("LG트레이딩");
        inbound2.setEmp_id(102);
        inbound2.setEmp_name("김철수");
        inbound2.setRemark("정기 입고");
        inboundMap.put(2, inbound2);

        Inbound inbound3 = new Inbound();
        inbound3.setInbound_id(3);
        inbound3.setStock_id(3);
        inbound3.setInbound_date(LocalDateTime.now());
        inbound3.setInbound_qty(10);
        inbound3.setUnit_price(80000);
        inbound3.setAc_id(203);
        inbound3.setAc_name("한국공구");
        inbound3.setEmp_id(103);
        inbound3.setEmp_name("이영희");
        inbound3.setRemark("추가 발주");
        inboundMap.put(3, inbound3);

        return inboundMap.getOrDefault(inbound_id, new Inbound());
    }

    // =============================================출고=============================================

    // 출고 목록 조회
    @GetMapping("/outbound")
    public List<Outbound> outboundList() {
        List<Outbound> outboundList = new ArrayList<>();

        Outbound outbound1 = new Outbound();
        outbound1.setOutbound_id(1);
        outbound1.setStock_id(1);
        outbound1.setOutbound_date(LocalDateTime.now().minusDays(1));
        outbound1.setOutbound_qty(3);
        outbound1.setAc_id(301);
        outbound1.setAc_name("서울창고");
        outbound1.setEmp_id(101);
        outbound1.setEmp_name("홍길동");
        outbound1.setRemark("정상 출고");
        outboundList.add(outbound1);

        Outbound outbound2 = new Outbound();
        outbound2.setOutbound_id(2);
        outbound2.setStock_id(2);
        outbound2.setOutbound_date(LocalDateTime.now().minusDays(2));
        outbound2.setOutbound_qty(5);
        outbound2.setAc_id(302);
        outbound2.setAc_name("부산물류센터");
        outbound2.setEmp_id(102);
        outbound2.setEmp_name("김철수");
        outbound2.setRemark("배송 완료");
        outboundList.add(outbound2);

        Outbound outbound3 = new Outbound();
        outbound3.setOutbound_id(3);
        outbound3.setStock_id(3);
        outbound3.setOutbound_date(LocalDateTime.now());
        outbound3.setOutbound_qty(2);
        outbound3.setAc_id(303);
        outbound3.setAc_name("대전지점");
        outbound3.setEmp_id(103);
        outbound3.setEmp_name("이영희");
        outbound3.setRemark("긴급 배송");
        outboundList.add(outbound3);

        return outboundList;
    }

    // 출고 단건 조회 (추가!)
    @GetMapping("/outbound/{outbound_id}")
    public Outbound getOutboundById(@PathVariable int outbound_id) {
        Map<Integer, Outbound> outboundMap = new HashMap<>();

        Outbound outbound1 = new Outbound();
        outbound1.setOutbound_id(1);
        outbound1.setStock_id(1);
        outbound1.setOutbound_date(LocalDateTime.now().minusDays(1));
        outbound1.setOutbound_qty(3);
        outbound1.setAc_id(301);
        outbound1.setAc_name("서울창고");
        outbound1.setEmp_id(101);
        outbound1.setEmp_name("홍길동");
        outbound1.setRemark("정상 출고");
        outboundMap.put(1, outbound1);

        Outbound outbound2 = new Outbound();
        outbound2.setOutbound_id(2);
        outbound2.setStock_id(2);
        outbound2.setOutbound_date(LocalDateTime.now().minusDays(2));
        outbound2.setOutbound_qty(5);
        outbound2.setAc_id(302);
        outbound2.setAc_name("부산물류센터");
        outbound2.setEmp_id(102);
        outbound2.setEmp_name("김철수");
        outbound2.setRemark("배송 완료");
        outboundMap.put(2, outbound2);

        Outbound outbound3 = new Outbound();
        outbound3.setOutbound_id(3);
        outbound3.setStock_id(3);
        outbound3.setOutbound_date(LocalDateTime.now());
        outbound3.setOutbound_qty(2);
        outbound3.setAc_id(303);
        outbound3.setAc_name("대전지점");
        outbound3.setEmp_id(103);
        outbound3.setEmp_name("이영희");
        outbound3.setRemark("긴급 배송");
        outboundMap.put(3, outbound3);

        return outboundMap.getOrDefault(outbound_id, new Outbound());
    }
}