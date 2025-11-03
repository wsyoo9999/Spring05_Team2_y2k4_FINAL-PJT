package com.multi.y2k4.vo.production;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Equipment {
    private Integer equipment_id;       // 설비코드
    private String equipment_name;      // 설비명
    private String equipment_status;    // 상태 (정상, 점검중, 고장)
    private LocalDate check_date;       // 점검일
}