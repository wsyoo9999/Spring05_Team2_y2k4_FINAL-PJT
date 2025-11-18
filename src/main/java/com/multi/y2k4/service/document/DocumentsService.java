package com.multi.y2k4.service.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.mapper.tenant.document.DocumentsMapper;
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

    public List<Documents> list(Integer doc_id,
                                Integer cat_id,
                                Integer tb_id,
                                Integer cd_id,
                                Integer req_id,
                                LocalDate req_date,
                                Integer appr_id,
                                LocalDate appr_date,
                                Integer status) {
        return documentsMapper.list(doc_id,cat_id, tb_id, cd_id, req_id, req_date, appr_id, appr_date, status);
    }

    public Documents searchById(Integer doc_id){
        return documentsMapper.searchById(doc_id);
    };


    int editStatus(Integer doc_id, Integer status){
        return documentsMapper.editStatus(doc_id, status);
    };

    @Transactional
    public void addDocument(Documents doc) {
        DocumentBodyBuilder documentBodyBuilder = new DocumentBodyBuilder(objectMapper);
        String body_html = documentBodyBuilder.buildBody(doc.getQuery());
        doc.setContent(body_html);
        documentsMapper.addDocument(doc);
    }
}
