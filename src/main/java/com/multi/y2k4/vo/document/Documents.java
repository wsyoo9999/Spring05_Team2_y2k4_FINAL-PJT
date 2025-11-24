package com.multi.y2k4.vo.document;

import lombok.Data;
import java.time.LocalDate;


@Data

public class Documents {
    private Long doc_id;             // 문서 ID (Primary Key)
    private int  cat_id;
    private int tb_id;
    private int cd_id;
    private String title;           // 제목
    private Long req_id;       // 기안자 ID
    private String req_name;
    private LocalDate req_date;  // 기안일
    private String content;         // 문서 내용
    private String comments;    //한줄평
    private int status;          // 결재 상태 (예: PENDING, APPROVED, REJECTED)
    private LocalDate appr_date; // 결재일
    private Long appr_id;        // 결재자 ID
    private String appr_name;
    private String query;
    private boolean is_checked;
}

