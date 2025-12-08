package com.ohgiraffers.queue.core.domain;

import com.ohgiraffers.common.constants.QueueConstants;
import com.ohgiraffers.common.constants.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitTimeEstimator {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 구매까지 처리까지 기다린 시간을 기록한다
     * PROCEED_QUEUE_CAPACITY만큼 최근의 평균을 계산한다
     * 단 PROCEED_QUEUE_TTL_MILLIS만큼 최대로 기다린 것은 제외한다
     * 
     * @param timedealId 타임딜 ID
     * @param processingTimeMs 구매까지 처리한 시간
     */
    public void recordProcessingTime(Long timedealId, long processingTimeMs) {

        if(processingTimeMs >= QueueConstants.PROCEED_QUEUE_TTL_MILLIS) {
            return;
        }

        String historyQueueKey = TimedealKeys.historyQueue(timedealId);
        stringRedisTemplate.opsForList().leftPush(historyQueueKey, String.valueOf(processingTimeMs));
        stringRedisTemplate.opsForList().trim(historyQueueKey, 0, QueueConstants.PROCEED_QUEUE_CAPACITY - 1);
    }

    /**
     * 대기열 예상 시간 계산
     * 
     * @param timedealId 타임딜 ID
     * @param position 대기열 순번
     * @return 대기열 예상 대기 시간 (초)
     */
    public long estimateWaitSeconds(Long timedealId, long position) {
        if (position <= 0) {
            return (QueueConstants.DEFAULT_PROCESSING_TIME_MILLIS / 1000);
        }

        String historyQueueKey = TimedealKeys.historyQueue(timedealId);
        List<String> history = stringRedisTemplate.opsForList().range(historyQueueKey, 0, -1);
        if(history == null || history.isEmpty()) {
            return (QueueConstants.DEFAULT_PROCESSING_TIME_MILLIS / 1000);
        }

        double avgMs = history.stream()
                .mapToLong(Long::parseLong)
                .average()
                .orElse(QueueConstants.DEFAULT_PROCESSING_TIME_MILLIS);

        return (long) ((position - 1) * avgMs / 1000);
    }
}
