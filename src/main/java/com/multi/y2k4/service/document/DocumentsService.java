package com.multi.y2k4.service.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.mapper.tenant.document.DocumentsMapper;
import com.multi.y2k4.service.alert.AlertService;
import com.multi.y2k4.vo.document.Documents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor

public class DocumentsService {
    private final DocumentsMapper documentsMapper;
    private final ObjectMapper objectMapper;
    private final AlertService alertService;

    public List<Documents> list(Integer doc_id,
                                Integer cat_id,
                                Integer tb_id,
                                Integer cd_id,
                                Integer req_id,
                                LocalDate req_date,
                                Integer appr_id,
                                LocalDate appr_date,
                                Integer status,
                                Integer member_id) {
        return documentsMapper.list(doc_id,cat_id, tb_id, cd_id, req_id, req_date, appr_id, appr_date, status, member_id);
    }

    public Documents searchById(Integer doc_id){
        return documentsMapper.searchById(doc_id);
    };


    public int editStatus(Integer doc_id, Integer status, String comments){
        int result = documentsMapper.editStatus(doc_id, status,comments);

        Documents doc = documentsMapper.searchById(doc_id);

        if (result > 0 && doc != null && doc.getReq_id() != null) {
            alertService.creatAlert(
                    doc.getReq_id(),  // 기안자 ID
                    doc.getDoc_id()   // 문서 ID
            );
        }

        return result;
//        return documentsMapper.editStatus(doc_id, status,comments);
    };

    @Transactional
    public void addDocument(Documents doc) {
        DocumentBodyBuilder documentBodyBuilder = new DocumentBodyBuilder(objectMapper);
        String body_html = documentBodyBuilder.buildBody(doc.getQuery());
        doc.setContent(body_html);
        alertService.createAlertForApprover(doc.getDoc_id(), 0);
    }
}
