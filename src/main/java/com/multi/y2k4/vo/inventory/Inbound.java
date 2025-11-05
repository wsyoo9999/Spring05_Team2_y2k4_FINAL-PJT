package com.multi.y2k4.vo.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Inbound {
    private int inbound_order;         // 입고번호 (PK)
    private int stock_id;            // 재고 코드 (FK)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime inbound_date;   // 입고시간
    private String stock_name;     // 물품명
    private int inbound_qty;        // 입고수량
    private int unit_price;         // 단가
    private String supplier;        // 공급업체
    private String manager;         // 담당자
    private String remark;          // 비고

    public void setRemark(String remark) {
        this.remark = Objects.requireNonNullElse(remark, "");
    }

    public int getTotal_price() {
        return inbound_qty * unit_price;
    }

}
