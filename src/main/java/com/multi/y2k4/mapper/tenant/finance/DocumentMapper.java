package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Documents;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocumentMapper {
    /**
     * 결재 문서 목록 조회 (필터링 포함)
     * @param requesterId 요청자 ID로 필터링
     * @param status 결재 상태로 필터링 (PENDING, APPROVED, REJECTED)
     * @return 결재 문서 목록
     */
    List<Documents> listDocuments(@Param("requesterId") Long requesterId, @Param("status") String status);
}