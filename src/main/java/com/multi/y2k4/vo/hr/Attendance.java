package com.multi.y2k4.vo.hr;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime; // LocalDateTime으로 변경

@Data
public class Attendance {
    private Integer attendance_id;          // 근태 기록 ID (PK)
    private Integer emp_id;                 // 사번 (FK)
    private String emp_name;                // 직원 이름
    private LocalDate work_date;            // 근무 일자
    private LocalDateTime check_in;         // 출근 시간
    private LocalDateTime check_out;        // 퇴근 시간
    private String attendance_status;       // 근무 상태
}