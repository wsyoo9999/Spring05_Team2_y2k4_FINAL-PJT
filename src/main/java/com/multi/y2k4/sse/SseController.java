package com.multi.y2k4.sse;

import com.multi.y2k4.service.alert.AlertService;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.vo.document.Documents;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterService sseEmitterService;
    private final DocumentsService documentsService;
    private final AlertService alertService;
    /**
     * 클라이언트의 이벤트 구독을 수락한다. text/event-stream은 SSE를 위한 Mime Type이다. 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
     */
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long userId) {    /*UserId는 로그인 사용자의 emp_id로 한다*/
        System.out.println("subscribe : " + userId);
        SseEmitter emitter = sseEmitterService.subscribe(userId);
        try {
            alertService.notifyDocCountChanged(userId);
        } catch (Exception e) {
            System.out.println("[SSE] subscribe 직후 알림 전송 중 예외: " + e.getMessage());
        }

        return emitter;
    }

//    /**
//     * 이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.
//     */
//    @PostMapping("/broadcast/{userId}")
//    public void broadcast(@PathVariable Long userId) {
//        Documents documents = new Documents();
//        documents.setReq_id(userId);
//        documents.setAppr_id(userId);
//        int unchecked = documentsService.searchByUnchecked(userId).size();
//        int unApprove = documentsService.searchByAppr(userId).size();
//        Integer unchecking = unchecked + unApprove;
//        sseEmitterService.broadcast(userId,unchecking);
//    }
}