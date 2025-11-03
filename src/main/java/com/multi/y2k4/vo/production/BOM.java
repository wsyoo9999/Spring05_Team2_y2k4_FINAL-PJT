package com.multi.y2k4.vo.production;

import lombok.Data;

@Data
public class BOM {
    private Integer bom_id;                 // BOM ID
    private String raw_materials_code;      // 원자재 코드
    private Integer item_id;                // 물품번호
    private Integer required_quantity;      // 소요량
}