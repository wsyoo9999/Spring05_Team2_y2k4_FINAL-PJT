package com.multi.y2k4.vo.item;

import lombok.Data;


@Data
public class Item {
    private int item_id;                // 물품번호 (PK)
    private String item_name;           // 물품명
    private int item_qty;               // 수량
    private int unit_price;             // 개당 가격
    private int item_status;            // 물품상태 (0=정상, 1=불량, 2=폐기, 3=반납, 4=보류)
    private String storage_location;    // 보관위치
}