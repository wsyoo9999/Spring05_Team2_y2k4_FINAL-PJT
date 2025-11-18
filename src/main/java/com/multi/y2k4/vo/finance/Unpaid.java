package com.multi.y2k4.vo.finance;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data

public class Unpaid {

    private Long unp_id;           // PK
    private int  cat_id;           // 카테고리
    private int  tb_id;            // 세부 카테고리
    private Long ref_pk;           // 참조 PK (sale_id, purchase_id 등)
    private Double cost;       // 현재 기준 미정산 금액
    private int  type;             // 1 수익, 2 지출
    private int  status;           // 0 미정산, 1 정산완료, 2 취소
    private LocalDateTime unpaid_date;    // 최초 미정산 발생일
    private LocalDateTime paid_date;      // 정산 완료일
}

