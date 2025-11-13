package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.SpendMapper;
import com.multi.y2k4.vo.finance.Spend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendService {

    private final SpendMapper spendMapper;

    /**
     * 지출 목록을 DB에서 조회합니다.
     * @param spendCode 필터링할 지출 코드
     * @param searchComment 필터링할 비고 키워드
     * @return 필터링된 Spend 목록
     */
    public List<Spend> listSpends(Integer spendCode, String searchComment) {
        // 실제로는 DB 쿼리 실행 전에 로그인된 사용자의 DB 스키마로 컨텍스트 전환 필요
        // 현재는 임시로 listSpends만 호출합니다.
        return spendMapper.listSpends(spendCode, searchComment);
    }
}
