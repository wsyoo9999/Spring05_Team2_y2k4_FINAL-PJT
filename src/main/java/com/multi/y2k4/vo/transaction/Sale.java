package com.multi.y2k4.vo.transaction;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class Sale {
    private int emp_id;
    private int client_id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate order_date;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate due_date;
    private String status;
}
