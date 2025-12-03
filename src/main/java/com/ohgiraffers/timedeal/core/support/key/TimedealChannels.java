package com.ohgiraffers.timedeal.core.support.key;

public class TimedealChannels {
    private static final String QUEUE_STATUS_PREFIX = "timedeal:queue:status:";
    private static final String QUEUE_STATUS_PATTERN = "timedeal:queue:status:*";

    public static String queueStatus(Long timedealId) {
        return QUEUE_STATUS_PREFIX + timedealId;
    }

    public static String queueStatusPattern() {
        return QUEUE_STATUS_PATTERN;
    }

    // SSE Event name
    public static final String SSE_QUEUE_STATUS = "queue-status";
}
