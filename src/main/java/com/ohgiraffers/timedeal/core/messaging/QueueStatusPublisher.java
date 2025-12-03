package com.ohgiraffers.timedeal.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.timedeal.core.api.controller.v1.message.QueueStatusEvent;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.core.support.key.TimedealChannels;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QueueStatusPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(QueueStatusEvent event) {
        try {
            String channel = TimedealChannels.queueStatus(event.dealId());
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    public void publishBatch(List<QueueStatusEvent> events) {
        events.forEach(this::publish);
    }
}
