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

import java.time.LocalDate;
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
    public List<Stock> listStock(@RequestParam(required=false) String stock_name,
                            @RequestParam(required=false) Integer qty,
                            @RequestParam(required=false) Integer unit_price,
                            @RequestParam(required=false) String location,
                            @RequestParam(required=false) Integer type) {

        boolean noFilter = (stock_name == null || stock_name.isBlank())
                && qty == null
                && unit_price == null
                && (location == null || location.isBlank())
                && type == null;

        return noFilter
                ? stockService.list_all()
                : stockService.list(stock_name, qty, unit_price, location, type);
    }

    // 재고 목록 추가
    @PostMapping("/stock/add")
    @Transactional
    public boolean addStock(@RequestParam String  stock_name,
                            @RequestParam Integer qty,
                            @RequestParam Integer unit_price,
                            @RequestParam String  location,
                            @RequestParam Integer type) {

        Stock stock = new Stock();
        stock.setStock_name(stock_name);
        stock.setQty(qty);
        stock.setUnit_price(unit_price);
        stock.setLocation(location);
        stock.setType(type);

        int affected = stockService.addStock(stock);
        return affected == 1;
    }

    @PutMapping(value = "/stock/{stock_id}", consumes = "application/json")
    public ResponseEntity<Void> update(@PathVariable("stock_id") Integer stock_id,
                                       @RequestBody Stock body) {
        body.setStock_id(stock_id);              // PK는 경로로 고정
        int updated = stockService.updateStock(body); // Mapper: UPDATE stock SET ... WHERE stock_id=?
        return (updated == 1) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }





    // =============================================입고=============================================

    @GetMapping("/inbound")
    public List<Inbound> listInbound(@RequestParam(required=false) LocalDate inbound_date,
                            @RequestParam(required=false) Integer stock_id,
                            @RequestParam(required=false) Integer ac_id,
                            @RequestParam(required=false) Integer emp_id,
                            @RequestParam(required=false) Integer approval) {

        boolean noFilter = (inbound_date == null)
                && (stock_id == null)
                && (ac_id == null)
                && (emp_id == null)
                && (approval == null);

        return noFilter
                ? inboundService.list_all()
                : inboundService.list(inbound_date, stock_id, ac_id, emp_id, approval);
    }

    // 재고 목록 추가
    @PostMapping("/inbound/add")
    @Transactional
    public boolean addInbound(@RequestParam LocalDate inbound_date,
                              @RequestParam Integer stock_id,
                              @RequestParam Integer inbound_qty,
                              @RequestParam Integer unit_price,
                              @RequestParam Integer ac_id,
                              @RequestParam Integer emp_id,
                              @RequestParam String remark,
                              @RequestParam(required=false, defaultValue = "0") Integer approval) {

        Inbound inbound = new Inbound();
        inbound.setInbound_date(inbound_date);
        inbound.setStock_id(stock_id);
        inbound.setInbound_qty(inbound_qty);
        inbound.setUnit_price(unit_price);
        inbound.setAc_id(ac_id);
        inbound.setEmp_id(emp_id);
        inbound.setRemark(remark);
        inbound.setApproval(approval);

        int affected = inboundService.addInbound(inbound);
        return affected == 1;
    }

    @PutMapping(value = "/inbound/{inbound_id}", consumes = "application/json")
    public ResponseEntity<Void> update(@PathVariable("inbound_id") Integer inbound_id,
                                       @RequestBody Inbound body) {
        body.setInbound_id(inbound_id);              // PK는 경로로 고정
        int updated = inboundService.updateInbound(body); // Mapper: UPDATE stock SET ... WHERE stock_id=?
        return (updated == 1) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


    // =============================================출고=============================================

    @GetMapping("/outbound")
    public List<Outbound> listOutbound(@RequestParam(required=false) LocalDate outbound_date,
                                     @RequestParam(required=false) Integer stock_id,
                                     @RequestParam(required=false) Integer ac_id,
                                     @RequestParam(required=false) Integer emp_id,
                                     @RequestParam(required=false) Integer approval) {

        boolean noFilter = (outbound_date == null)
                && (stock_id == null)
                && (ac_id == null)
                && (emp_id == null)
                && (approval == null);

        return noFilter
                ? outboundService.list_all()
                : outboundService.list(outbound_date, stock_id, ac_id, emp_id, approval);
    }

    // 재고 목록 추가
    @PostMapping("/outbound/add")
    @Transactional
    public boolean addOutbound(@RequestParam LocalDate outbound_date,
                              @RequestParam Integer stock_id,
                              @RequestParam Integer outbound_qty,
                              @RequestParam Integer ac_id,
                              @RequestParam Integer emp_id,
                              @RequestParam String remark,
                              @RequestParam(required=false, defaultValue = "0") Integer approval) {

        Outbound outbound = new Outbound();
        outbound.setOutbound_date(outbound_date);
        outbound.setStock_id(stock_id);
        outbound.setOutbound_qty(outbound_qty);
        outbound.setAc_id(ac_id);
        outbound.setEmp_id(emp_id);
        outbound.setRemark(remark);
        outbound.setApproval(approval);

        int affected = outboundService.addOutbound(outbound);
        return affected == 1;
    }

    @PutMapping(value = "/outbound/{outbound_id}", consumes = "application/json")
    public ResponseEntity<Void> updateOutbound(@PathVariable("outbound_id") Integer outbound_id,
                                       @RequestBody Outbound body) {
        body.setOutbound_id(outbound_id);              // PK는 경로로 고정
        int updated = outboundService.updateOutbound(body); // Mapper: UPDATE stock SET ... WHERE stock_id=?
        return (updated == 1) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}