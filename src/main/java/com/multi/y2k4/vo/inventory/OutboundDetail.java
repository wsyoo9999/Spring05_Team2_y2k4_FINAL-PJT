package com.multi.y2k4.vo.inventory;

import lombok.Data;

@Data
public class OutboundDetail {
    private Integer od_id;          // 출고 상세 번호
    private Integer outbound_id;    // 출고번호
    private Integer stock_id;       // 재고번호
    private Integer qty;        // 수량
    private Double unit_price;  // 단가

    // 조인 조회 시 추가 정보
    private String stock_name;  // 재고명
    private Double total_price; // qty * unit_price
}