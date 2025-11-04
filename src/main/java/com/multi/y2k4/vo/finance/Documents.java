package com.multi.y2k4.vo.finance;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Lombok 사용을 가정하여 @Data를 사용하거나, 직접 Getter/Setter를 추가합니다.
// (여기서는 Lombok이 없다고 가정하고 필요한 필드만 정의합니다)
public class Documents {
    private int docId;          // 결재 문서 ID (기본 키)
    private String docTitle;    // 문서 제목
    private int empId;          // 기안자 ID
    private LocalDate docDate;  // 기안일
    private String docContent;  // 문서 내용 (JSON 등 복잡한 데이터 포함 가능)
    private String status;      // 결재 상태 (예: 'DRAFT', 'PENDING', 'APPROVED', 'REJECTED')
    private int approverId;     // 최종 승인자 ID
    private LocalDateTime approvalDate; // 승인일시

    // 이외 필요한 필드 (예: 금액, 관련 전표 ID 등)와
    // Getter, Setter, 생성자는 Lombok(@Data) 또는 직접 추가합니다.
}