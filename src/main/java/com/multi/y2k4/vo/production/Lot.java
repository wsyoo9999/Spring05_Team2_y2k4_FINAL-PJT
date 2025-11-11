package com.multi.y2k4.vo.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Lot {
    private Long lot_id;           // Lot ID
    private Long work_order_id;    // 작업지시 번호
    private Long stock_id;         // 자재(품목) 번호
    private Integer lot_qty;       // Lot 수량
    private LocalDate lot_date;    // Lot 생성일
}
