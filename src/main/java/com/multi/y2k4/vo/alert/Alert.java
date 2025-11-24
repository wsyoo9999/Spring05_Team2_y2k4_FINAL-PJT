package com.multi.y2k4.vo.alert;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Alert {
    private int alert_id;
    private Long emp_id;    // 기안자, 결재자 구분용 현재 사용자 사번
    private Long doc_id;    // 조인을 위한 fk
    private Integer alert_type; // 0: 결재자 알림 1: 기안자 알림
    private Integer is_read;
    private LocalDateTime created_at;

    // 조인으로 가져올 필드
    private String doc_title;
    private int doc_status;
    private String req_name;// 기안자
    private String appr_name;// 결재자
}
