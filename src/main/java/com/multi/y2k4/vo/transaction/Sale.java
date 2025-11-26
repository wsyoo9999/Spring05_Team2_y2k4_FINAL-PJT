package com.multi.y2k4.vo.transaction;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class Sale {
    private int sale_id;
    private int emp_id;
    private String emp_name;
    private int ac_id;
    private String ac_name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate order_date;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate due_date;
    private double total_price;
    private int status;

    // 대시보드 출력용
    private String month;
    private Long total_price_sum;
}
