package com.multi.y2k4.vo.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@AllArgsConstructor
public class Slips {
    private Long slipId;            // 전표 ID (Primary Key)
    private Long docId;             // 연결된 결재 문서 ID (Foreign Key)
    private LocalDate slipDate;     // 전표 일자
    private String acctCode;        // 계정 코드
    private String acctName;        // 계정 이름
    private BigDecimal debitAmount; // 차변 금액
    private BigDecimal creditAmount; // 대변 금액
    private String remark;          // 적요
    private String transferStatus;  // ERP 전송 상태 (예: READY, TRANSFERED)
}