package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Profit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProfitMapper {
    /**
     * 수익 목록 조회 (필터링 포함)
     * @param profitCode 수익 원인 코드
     * @param searchComment 비고 검색 키워드
     * @return 수익 목록
     */
    List<Profit> listProfits(@Param("profitCode") Integer profitCode, @Param("searchComment") String searchComment);
}