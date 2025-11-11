package com.multi.y2k4.vo.finance;

import lombok.Data;
import lombok.AllArgsConstructor; // 👈 이 어노테이션을 추가합니다.
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동 생성
public class Documents {
    private Long docId;             // 문서 ID (Primary Key)
    private String title;           // 제목
    private Long requesterId;       // 기안자 ID
    private LocalDate requestDate;  // 기안일
    private String content;         // 문서 내용
    private String status;          // 결재 상태 (예: PENDING, APPROVED, REJECTED)
    private LocalDate approvalDate; // 결재일
    private Long approverId;        // 결재자 ID
}