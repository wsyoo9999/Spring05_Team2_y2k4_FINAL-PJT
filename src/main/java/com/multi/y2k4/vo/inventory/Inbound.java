package com.multi.y2k4.vo.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Inbound {
    private int inbound_order;         // 입고번호 (PK)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime inbound_date;   // 입고시간
    private int stock_id;   // 재고 코드(FK)
    private int inbound_qty;        // 입고수량
    private int unit_price;         // 단가
    private int ac_id;  // 업체id(FK)
    private String ac_name; // 조인으로 받아오기
    private int emp_id;  // 사원id(FK)
    private String emp_name; // 조인으로 받아오기
    private String remark;          // 비고

    public int getTotal_price() {
        return inbound_qty * unit_price;
    }

}
