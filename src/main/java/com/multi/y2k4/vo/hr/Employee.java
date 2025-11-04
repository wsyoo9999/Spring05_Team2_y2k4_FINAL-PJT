package com.multi.y2k4.vo.hr;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Employee {
    private Integer emp_id;         // 사번
    private String emp_name;        // 이름
    private String dept_name;       // 부서명
    private String position;        // 직급
    private LocalDate hire_date;    // 입사일
    private String status;          // 재직상태 (재직, 휴직, 퇴사)
    private String phone_number;    // 연락처
}