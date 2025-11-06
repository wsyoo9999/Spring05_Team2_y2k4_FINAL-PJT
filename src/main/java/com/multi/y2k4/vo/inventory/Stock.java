package com.multi.y2k4.vo.inventory;

import lombok.Data;


@Data
public class Stock {
    private int stock_id;                // 재고번호 (PK)
    private String stock_name;           // 재고명
    private int stock_qty;               // 수량
    private int unit_price;             // 판매 단가
    private int item_status;            // 물품상태 (0=정상, 1=불량, 2=폐기, 3=반납, 4=보류)
    private String storage_location;    // 보관위치
    private String expiration_date;     // 유통기한
    private int gubun;               // 구분 (0=원자재, 1=판매상품)
}