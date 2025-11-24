package com.multi.y2k4.mapper.tenant.document;


import com.multi.y2k4.vo.document.Documents;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DocumentsMapper {
    List<Documents> list(@Param("doc_id") Integer doc_id,
                         @Param("cat_id") Integer cat_id,
                         @Param("tb_id") Integer tb_id,
                         @Param("cd_id") Integer cd_id,
                         @Param("req_id") Integer req_id,
                         @Param("req_date")  LocalDate req_date,
                         @Param("appr_id") Integer appr_id,
                         @Param("appr_date") LocalDate appr_date,
                         @Param("status") Integer status,
                         @Param("member_id") Integer member_id);

    Documents searchById(@Param("doc_id") Integer doc_id);

    int addDocument(Documents document);

    int editStatus(@Param("doc_id") Integer doc_id,
                   @Param("status") Integer status,
                   @Param("comments") String comments);

    List<Documents> searchByUnchecked(Documents doc);

    List<Documents> searchByAppr(Documents doc);

    int read(@Param("doc_id") Integer docId);
}
