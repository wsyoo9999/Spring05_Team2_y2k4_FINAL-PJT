package com.multi.y2k4.vo.finance;

import lombok.Data;
import lombok.AllArgsConstructor; // 👈 이 어노테이션을 추가합니다.
import java.math.BigDecimal;

@Data
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동 생성
public class Budget {
    private String acctCode;        // 예산 계정 코드 (Primary Key)
    private String acctName;        // 예산 계정 이름
    private BigDecimal annualBudget; // 연간 예산 금액
    private BigDecimal remains;      // 현재 잔액
}