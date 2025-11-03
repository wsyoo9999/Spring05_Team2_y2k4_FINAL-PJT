package com.multi.y2k4.vo.finance;

import java.time.LocalDate;

public class Documents {
    private Long docId;
    private String title;
    private String status;
    private LocalDate submitDate;

    // 1. 기본 생성자 (필수: Spring이 JSON 요청 본문을 객체화할 때 사용)
    public Documents() {
    }

    // 2. 전체 인자 생성자 (필수: Controller에서 테스트 데이터를 초기화할 때 사용)
    public Documents(Long docId, String title, String status, LocalDate submitDate) {
        this.docId = docId;
        this.title = title;
        this.status = status;
        this.submitDate = submitDate;
    }

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getSubmitDate() { return submitDate; }
    public void setSubmitDate(LocalDate submitDate) { this.submitDate = submitDate; }
}