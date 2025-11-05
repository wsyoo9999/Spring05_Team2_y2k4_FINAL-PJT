package com.multi.y2k4.vo.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Outbound {
    private int outbound_id;        // 출고 번호 (PK)
    private int stock_id;            // 재고 코드 (FK)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime outbound_date;  // 출고일
    private int outbound_qty;       // 수량
    private String outbound_location;      // 출고처
    private String manager;         // 담당자
    private String remark;
}