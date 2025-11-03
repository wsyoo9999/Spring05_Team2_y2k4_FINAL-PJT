package com.multi.y2k4.vo.production;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Defect {
    private Integer defect_id;          // 불량코드
    private String defect_name;         // 불량명
    private Integer defect_quantity;    // 불량 수량
    private LocalDate detected_date;    // 등록일
    private Integer order_id;           // 작업지시번호
}