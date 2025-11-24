package com.multi.y2k4.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class EmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter findById(Long user_id) {
        return emitters.get(user_id);
    }

    public SseEmitter save(Long user_id, SseEmitter sseEmitter) {
        emitters.put(user_id, sseEmitter);
        return emitters.get(user_id);
    }

    public void deleteById(Long user_id) {
        emitters.remove(user_id);
    }
}
