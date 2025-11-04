package com.multi.y2k4.vo.finance;

import java.time.LocalDate;

public class Slips {
    private int slipId;         // 전표 ID (기본 키)
    private int docId;          // 관련 결재 문서 ID (Documents의 docId)
    private LocalDate slipDate; // 전표 생성일
    private String transferStatus; // ERP 전송 상태 (예: 'READY', 'TRANSFERRING', 'COMPLETE')
    private String slipDetails; // 전표 상세 항목 (차변/대변 계정, 금액 등 복잡한 정보)

    // 이외 필요한 필드와 Getter, Setter, 생성자는 추가합니다.
}