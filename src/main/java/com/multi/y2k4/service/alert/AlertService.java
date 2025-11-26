package com.multi.y2k4.service.alert;

import com.multi.y2k4.mapper.tenant.document.DocumentsMapper;
import com.multi.y2k4.sse.SseEmitterService;
import com.multi.y2k4.vo.document.Documents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final DocumentsMapper documentsMapper;
    private final SseEmitterService sseEmitterService;

    /**
     * memberId 기준으로 "내가 처리해야 할 문서 개수" 계산해서 SSE로 송신
     * - 기안자: 내가 만든 문서 중 아직 안 읽은 것(checked = false)
     * - 결재자: 내가 결재해야 하는 문서 중 status = 0(대기)
     */
    public void notifyDocCountChanged(Long emp_id) {
        if (emp_id == null) return;
        if (!sseEmitterService.isSubscribed(emp_id)) {
            return;
        }
        try {
            // 1) 내가 기안한 문서 중 아직 안 읽은 것
            Documents cond1 = new Documents();
            cond1.setReq_id(emp_id);
            int unchecked = documentsMapper.searchByUnchecked(cond1).size();

            // 2) 내가 결재자인데 아직 처리 안 된 문서들
            Documents cond2 = new Documents();
            cond2.setAppr_id(emp_id);
            int unApprove = documentsMapper.searchByAppr(cond2).size();

            int total = unchecked + unApprove;
            System.out.println("unchecked : "+ unchecked);
            System.out.println("unApprove : "+unApprove);
            System.out.println("total : " + total);
            // SSE로 전송
            sseEmitterService.broadcast(emp_id, total);
        } catch (Exception e) {
            // 여기서 예외를 먹어버리면 컨트롤러까지 안 올라감
            System.out.println("[AlertService] notifyDocCountChanged 중 예외 발생 emp_id=" + emp_id + " / " + e);
        }
    }

    /**
     * 새 문서가 생성되었을 때 호출:
     * - 보통 결재자에게 카운트 갱신
     */
    public void onDocumentCreated(Long appr_id) {
            notifyDocCountChanged(appr_id);
    }

    /**
     * 문서 상태(승인/반려 등)가 바뀌었을 때 호출:
     */
    public void onDocumentStatusChanged(Long req_id) {
        notifyDocCountChanged(req_id);
    }
}