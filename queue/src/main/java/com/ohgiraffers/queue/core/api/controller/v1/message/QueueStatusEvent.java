package com.ohgiraffers.queue.core.api.controller.v1.message;

import com.ohgiraffers.queue.core.enums.QueueStatus;

public record QueueStatusEvent(
        Long userId,
        Long dealId,
        Long position,
        Long totalWaiting,
        Long estimatedSeconds,
        QueueStatus status
) {
    public static QueueStatusEvent of(Long userId, Long dealId, QueueStatus status) {
        return new QueueStatusEvent(userId, dealId, 0L, 0L, 0L, status);
    }
}
