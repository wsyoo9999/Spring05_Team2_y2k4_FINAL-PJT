package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.ProfitMapper;
import com.multi.y2k4.vo.finance.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfitService {

    private final ProfitMapper profitMapper;

    /**
     * 수익 목록을 DB에서 조회합니다.
     * @param profitCode 필터링할 수익 코드
     * @param searchComment 필터링할 비고 키워드
     * @return 필터링된 Profit 목록
     */
    public List<Profit> listProfits(Integer profitCode, String searchComment) {
        // 실제로는 DB 쿼리 실행 전에 로그인된 사용자의 DB 스키마로 컨텍스트 전환 필요
        // 현재는 임시로 listProfits만 호출합니다.
        return profitMapper.listProfits(profitCode, searchComment);
    }
}