package com.multi.y2k4.vo.finance;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Spend {

    private Long spend_id;         // PK
    private int  cat_id;           // 카테고리
    private int  tb_id;            // 세부 카테고리
    private Double spend;      // 지출 금액
    private LocalDateTime spend_date;     // 지출 일자
    private String spend_comment;  // 메모
}
