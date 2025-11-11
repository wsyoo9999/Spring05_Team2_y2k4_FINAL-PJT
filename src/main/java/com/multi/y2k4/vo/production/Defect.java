package com.multi.y2k4.vo.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Defect {
    private Long defect_code;      // 불량 코드
    private Long work_order_id;    // 작업지시 번호
    private Long lot_id;           // Lot 번호
    private Long stock_id;         // 자재(품목) 번호
    private Integer defect_qty;    // 불량 수량
    private LocalDate defect_date; // 불량 등록일
}
