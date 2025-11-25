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

}