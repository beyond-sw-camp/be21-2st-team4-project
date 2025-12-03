package com.ohgiraffers.timedeal.core.messaging;

import com.ohgiraffers.timedeal.core.api.controller.v1.message.QueueStatusEvent;
import com.ohgiraffers.timedeal.core.support.key.TimedealChannels;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 60 * 1000L;

    public SseEmitter createEmitter(Long userId) {
        removeEmitter(userId);

        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
        });

        emitter.onError(e -> {
            emitters.remove(userId);
        });

        emitters.put(userId, emitter);
        return emitter;
    }

    public void send(Long userId, QueueStatusEvent message) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(TimedealChannels.SSE_QUEUE_STATUS)
                    .data(message, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitters.remove(userId);
        }
    }

    public boolean hasConnection(Long userId) {
        return emitters.containsKey(userId);
    }

    public void removeEmitter(Long userId) {
        SseEmitter existing = emitters.remove(userId);
        if (existing != null) {
            existing.complete();
        }
    }

    public Set<Long> getAllConnectedUsers() {
        return new HashSet<>(emitters.keySet());
    }
}
