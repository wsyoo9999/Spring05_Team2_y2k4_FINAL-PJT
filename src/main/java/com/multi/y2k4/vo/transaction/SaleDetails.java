package com.multi.y2k4.vo.transaction;

import lombok.Data;

@Data
public class SaleDetails {
    private int sd_id;
    private int sale_id;
    private int stock_id;
    private int remain_qty;
    private String stock_name;
    private int qty;
    private double price_per_unit;
    private double total_price;
}
