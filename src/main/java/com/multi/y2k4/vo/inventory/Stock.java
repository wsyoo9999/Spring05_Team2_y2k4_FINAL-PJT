package com.multi.y2k4.vo.inventory;

import lombok.Data;


@Data
public class Stock {
    private int stock_id;                // 재고번호 (PK)
    private String stock_name;           // 재고명
    private int qty;               // 수량
    private int unit_price;             // 단가
    private String location;    // 보관위치
    private int type;               // 구분 (0=원자재, 1=판매상품)
    private int is_deleted;     // 삭제 여부(0:삭제 안됨 1:삭제됨
}