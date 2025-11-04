package com.multi.y2k4.vo.hr;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Attendance {
    private Integer attendance_id;  // 근태 기록 ID (PK)
    private Integer emp_id;         // 사번 (FK)
    private String emp_name;        // 직원 이름 (Join용)
    private LocalDate att_date;     // 날짜
    private LocalTime check_in;     // 출근 시간
    private LocalTime check_out;    // 퇴근 시간
    private String status;          // 근무 상태 (정상, 지각, 조퇴, 결근)
}