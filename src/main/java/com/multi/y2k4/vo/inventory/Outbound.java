package com.multi.y2k4.vo.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Outbound {
    private int outbound_id;        // 출고 번호 (PK)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime outbound_date;  // 출고일
    private int stock_id;            // 재고 코드(FK)
    private int outbound_qty;       // 수량
    private int ac_id;  // 업체id(FK)
    private String ac_name; // 조인으로 받아오기
    private int emp_id;  // 사원id(FK)
    private String emp_name; // 조인으로 받아오기
    private String remark;
    private int approval;   // 결재승인상태 (0:대기, 1:승인, 2:반려)
}