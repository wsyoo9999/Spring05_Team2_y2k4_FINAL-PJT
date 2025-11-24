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

        if (result > 0) {
            Documents doc = documentsMapper.searchById(doc_id);
            if (doc != null) {
                // 기안자 & 결재자 둘 다 카운트 갱신
                alertService.onDocumentStatusChanged(doc.getReq_id());
            }
        }
        return result;
    };

    @Transactional
    public void addDocument(Documents doc) {
        DocumentBodyBuilder documentBodyBuilder = new DocumentBodyBuilder(objectMapper);
        String body_html = documentBodyBuilder.buildBody(doc.getQuery());
        doc.setContent(body_html);
        documentsMapper.addDocument(doc);
        alertService.onDocumentCreated(doc.getAppr_id());
    }

    public List<Documents> searchByUnchecked(Long req_id){
        Documents doc = new Documents();
        doc.setReq_id(req_id);
        return documentsMapper.searchByUnchecked(doc);
    }

    public List<Documents> searchByAppr(Long appr_id){
        Documents doc = new Documents();
        doc.setAppr_id(appr_id);
        return documentsMapper.searchByAppr(doc);
    }

    public int read(Integer doc_id){
        return documentsMapper.read(doc_id);
    }

}
