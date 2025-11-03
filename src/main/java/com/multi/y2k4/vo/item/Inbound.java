package com.multi.y2k4.vo.item;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Inbound {
    private int inbound_id;         // 입고번호 (PK)
    private int item_id;            // 물품아이디 (FK)
    private LocalDateTime inbound_date;   // 입고시간
    private int inbound_qty;        // 입고수량
    private int unit_price;         // 단가
    private int total_price;        // 총금액 (inbound_qty * unit_price)
    private String supplier;        // 공급업체
    private LocalDate expand_date;  // 소비기한
}
