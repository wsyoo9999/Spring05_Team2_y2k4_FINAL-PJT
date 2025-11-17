package com.multi.y2k4.vo.inventory;

import lombok.Data;


@Data
public class Stock {
    private Integer stock_id;                // 재고번호 (PK)
    private String stock_name;           // 재고명
    private Integer qty;               // 수량
    private Integer unit_price;             // 단가
    private String location;    // 보관위치
    private Integer type;               // 구분 (0=원자재, 1=판매상품)
    private Integer is_deleted;     // 삭제 여부(0:삭제 안됨 1:삭제됨
}