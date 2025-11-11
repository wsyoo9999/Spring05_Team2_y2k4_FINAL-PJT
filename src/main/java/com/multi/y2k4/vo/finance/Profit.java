package com.multi.y2k4.vo.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profit {
    private Long profitId;      // 수익 고유 번호 (BIGINT, PK)
    private Integer profitCode; // 수익 원인 코드 (INT)
    private BigDecimal profit;  // 수익 액수 (DECIMAL(15,2))
    private LocalDateTime profitDate; // 수익 일자 (DATETIME)
    private String profitComment; // 비고 (VARCHAR(100))
}