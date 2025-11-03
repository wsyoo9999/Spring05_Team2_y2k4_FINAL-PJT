package com.multi.y2k4.vo.production;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Lot {
    private Integer lot_id;             // Lot ID
    private String lot_number;          // Lot번호
    private Integer item_id;            // 물품번호
    private LocalDate production_date;  // 제조날짜
    private Integer lot_quantity;       // Lot수량
    private Integer order_id;           // 작업지시번호
}