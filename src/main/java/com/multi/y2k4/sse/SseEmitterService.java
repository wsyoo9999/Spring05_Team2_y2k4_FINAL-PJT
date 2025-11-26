package com.multi.y2k4.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    // SSE 이벤트 타임아웃 시간 (1시간)
    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000;

    private final EmitterRepository emitterRepository;

    /**
     * 클라이언트의 이벤트 구독을 허용하는 메서드
     * - 새로고침 등으로 동일 userId가 다시 구독해도 이전 emitter를 정리하고 새로 교체
     */
    public SseEmitter subscribe(Long userId) {

        // 새로고침 시 안전하게 교체
        SseEmitter old = emitterRepository.findById(userId);
        if (old != null) {
            try {
                old.complete();
            } catch (Exception ignored) {
            }
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] 기존 emitter 정리 userId=" + userId);
        }

        // 새 emitter 생성 및 저장
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        // ✅ 3) 콜백 등록 (완료 / 타임아웃 / 에러 시 repository에서 제거)
        sseEmitter.onCompletion(() -> {
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] onCompletion, emitter 제거 userId=" + userId);
        });

        sseEmitter.onTimeout(() -> {
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] onTimeout, emitter 제거 userId=" + userId);
        });

        sseEmitter.onError(e -> {
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] onError, emitter 제거 userId=" + userId + " / " + e);
        });

        // ✅ 4) 첫 구독시에 이벤트를 발생시켜서 503 방지 + 연결 확인
        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id("INIT-" + userId)
                            .name("sse-init")
                            .data("subscribe event, userId : " + userId)
            );
        } catch (IOException ex) {
            // 최초 전송조차 실패하면 emitter 정리
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] 초기 전송 오류, emitter 제거 userId=" + userId + " / " + ex.getMessage());
        }

        return sseEmitter;
    }

    public void unsubscribe(Long userId) {
        SseEmitter emitter = emitterRepository.findById(userId);
        if (emitter != null) {
            try {
                emitter.complete();  // 연결 종료 신호
            } catch (Exception ignored) {
            }
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] unsubscribe, emitter 제거 userId=" + userId);
        }
    }

    /**
     * AlertService 등에서 호출하는 진입점
     */
    public void broadcast(Long userId, Integer unchecking) {
        // 최후의 방어선: 어떤 예외도 밖으로 나가지 않게 함
        try {
            sendToClient(userId, unchecking);
        } catch (Exception e) {
            System.out.println("[SSE] broadcast 중 예외 무시 userId=" + userId + " / " + e);
        }
    }

    /**
     * 실제 전송 로직
     */
    private void sendToClient(Long userId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);
        if (sseEmitter == null) {
            System.out.println("[SSE] userId=" + userId + " emitter 없음. (아직 구독 안 함 / 끊김)");
            return;
        }
        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id("MSG-" + userId + "-" + System.currentTimeMillis())
                            .name("sse")
                            .data(data)
            );
        } catch (IOException ex) {
            // ✅ 여기서 네트워크 끊김(브라우저 닫힘/새로고침) 등 에러 발생
            emitterRepository.deleteById(userId);
            System.out.println("[SSE] 전송 중 오류, emitter 제거 userId=" + userId + " / " + ex.getMessage());
            try {
                sseEmitter.complete();
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isSubscribed(Long userId) {
        return emitterRepository.findById(userId) != null;
    }
}
