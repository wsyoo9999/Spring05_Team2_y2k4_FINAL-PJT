package com.multi.y2k4.vo.finance;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Profit {

    private Long profit_id;        // PK
    private int  cat_id;           // 카테고리 (0 재무, 1 판매/구매, ...)
    private int  tb_id;            // 세부 카테고리 (0: 판매, 1: 구매, ...)
    private Integer profit_code;   // 수익 코드 (nullable)
    private Double profit;     // 수익 금액
    private LocalDateTime profit_date;    // 수익 발생일/정산일
    private String profit_comment; // 메모
}
