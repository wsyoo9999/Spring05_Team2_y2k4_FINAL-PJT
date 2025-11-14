package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Spend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpendMapper {
    /**
     * 지출 목록 조회 (필터링 포함)
     * @param spendCode 지출 원인 코드
     * @param searchComment 비고 검색 키워드
     * @return 지출 목록
     */
    List<Spend> listSpends(@Param("spendCode") Integer spendCode, @Param("searchComment") String searchComment);
}