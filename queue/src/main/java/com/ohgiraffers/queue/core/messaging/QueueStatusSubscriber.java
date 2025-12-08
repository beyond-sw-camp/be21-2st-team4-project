package com.ohgiraffers.queue.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.queue.core.api.controller.v1.message.QueueStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueStatusSubscriber {
    private final SseEmitterRegistry emitterRegistry;
    private final ObjectMapper objectMapper;

    public void onMessage(String message, String channel) {
        try {
            QueueStatusEvent event = objectMapper.readValue(message, QueueStatusEvent.class);
            emitterRegistry.send(event.userId(), event);
        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
