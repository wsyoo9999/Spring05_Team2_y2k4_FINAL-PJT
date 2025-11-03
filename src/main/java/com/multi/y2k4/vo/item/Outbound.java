package com.multi.y2k4.vo.item;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Outbound {
    private int outbound_id;        // 출고번호 (PK)
    private int item_id;            // 물품아이디 (FK)
    private LocalDateTime outbound_date;  // 출고시간
    private int outbound_qty;       // 출고수량
    private int unit_price;         // 단가
    private int total_price;        // 출고총액
}
