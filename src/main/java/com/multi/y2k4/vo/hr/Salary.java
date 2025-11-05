package com.multi.y2k4.vo.hr;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Salary {
    private Integer salary_id;          // 급여 기록 ID (PK)
    private Integer emp_id;             // 사번 (FK)
    private String emp_name;            // 직원 이름
    private LocalDate payment_date;     // 지급일자
    private Integer basic_salary;       // 기본급 (계산용)
    private Integer allowance;          // 수당 (계산용)
    private Integer total_gross;        // 총 지급액 (기본급 + 수당)
    private Integer deduction_amount;   // 총 공제액
    private Integer total_pay;          // 실수령액
    private String bank_name;           // 은행명 (신규 추가)
    private String account_number;      // 계좌번호 (신규 추가)
}