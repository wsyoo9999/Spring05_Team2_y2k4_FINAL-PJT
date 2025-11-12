package com.multi.y2k4.vo.transaction;


import lombok.Data;

@Data
public class PurchaseDetails {
    private int pd_id;
    private int purchase_id;
    private int stock_id;
    private String stock_name;
    private int purchase_qty;   //발주 수량
    private int qty;        //도착 수량
    private double price_per_unit;
    private double total_price;
}
