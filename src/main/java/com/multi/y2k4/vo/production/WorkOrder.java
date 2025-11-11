package com.multi.y2k4.vo.production;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkOrder {
    private Long work_order_id;         // 작업지시 번호
    private Long stock_id;              // 자재(품목) 번호
    private Long emp_id;                // 담당 사원 번호
    private LocalDate start_date;       // 시작일
    private LocalDate due_date;         // 완료 예정일
    private Integer target_qty;         // 목표 수량
    private Integer good_qty;           // 양품 수량
    private Integer defect_qty;         // 불량 수량
    private String order_status;        // 상태 (대기, 진행중, 완료 등)
    private LocalDateTime request_date; // 지시 등록일시
}
