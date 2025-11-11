package com.multi.y2k4.vo.production;

import lombok.Data;

@Data
public class BOM {
    private Long parent_stock_id;   // 완제품(상위) 품목 ID
    private Long child_stock_id;    // 자재(하위) 품목 ID
    private Integer required_qty;   // 필요 수량
}
