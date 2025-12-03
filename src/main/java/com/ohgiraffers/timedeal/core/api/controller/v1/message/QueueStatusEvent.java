package com.ohgiraffers.timedeal.core.api.controller.v1.message;

import com.ohgiraffers.timedeal.core.enums.QueueStatus;

public record QueueStatusEvent(
        Long userId,
        Long dealId,
        Long position,
        Long totalWaiting,
        Long estimatedSeconds,
        QueueStatus status
) {

}
