package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.DocumentMapper;
import com.multi.y2k4.vo.finance.Documents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;

    /**
     * 결재 문서 목록을 DB에서 조회합니다.
     * @param requesterId 요청자 ID로 필터링
     * @param status 결재 상태로 필터링
     * @return 필터링된 Documents 목록
     */
    public List<Documents> listDocuments(Long requesterId, String status) {
        return documentMapper.listDocuments(requesterId, status);
    }
}