package com.multi.y2k4.vo.finance;

public class Budget {
    private String acctCode;    // 예산 계정 코드 (기본 키)
    private String acctName;    // 계정 명
    private long annualBudget;  // 연간 예산 총액
    private long remains;       // 예산 잔액
    private String remarks;     // 비고

    // 이외 필요한 필드와 Getter, Setter, 생성자는 추가합니다.
}