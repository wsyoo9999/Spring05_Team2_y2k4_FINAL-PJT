package com.multi.y2k4.vo.production;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkOrder {
    private Integer order_id;           // 작업지시번호
    private Integer item_id;            // 물품번호
    private LocalDate start_date;       // 시작일
    private LocalDate due_date;         // 완료일
    private Integer target_quantity;    // 목표수량
    private Integer good_quantity;      // 양품수량
    private Integer defect_quantity;    // 불량수량
    private String order_status;        // 상태 (대기, 진행중, 완료)
}