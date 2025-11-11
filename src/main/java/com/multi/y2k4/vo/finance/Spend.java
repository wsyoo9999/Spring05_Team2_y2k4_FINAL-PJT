package com.multi.y2k4.vo.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spend {
    private Long spendId;      // 지출 고유 번호 (BIGINT, PK)
    private Integer spendCode; // 지출 원인 코드 (INT)
    private BigDecimal spend;  // 지출 액수 (DECIMAL(15,2))
    private LocalDateTime spendDate; // 지출 일자 (DATETIME)
    private String spendComment; // 비고 (VARCHAR(100))
}