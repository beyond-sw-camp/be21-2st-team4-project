package com.ohgiraffers.timedeal.core.api.controller.scheduler;

import com.ohgiraffers.timedeal.core.domain.QueueService;
import com.ohgiraffers.timedeal.core.enums.QueueStatus;
import com.ohgiraffers.timedeal.core.api.controller.v1.message.QueueStatusEvent;
import com.ohgiraffers.timedeal.core.messaging.QueueStatusPublisher;
import com.ohgiraffers.timedeal.core.support.key.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final QueueService queueService;
    private final QueueStatusPublisher publisher;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 대기열 처리를 10초마다 처리한다
     */
    @Scheduled(fixedRate = 10000)
    public void scheduled() {

        // 활성화된 프로모션 조회
        List<Long> timedeal = getActivePromotion();
        if (timedeal.isEmpty()) {
            return;
        }

        // 대기열 변화를 전송할 event list
        List<QueueStatusEvent> events = new ArrayList<>();

        // 프로모션별 완료 상태를 처리한다
        for (Long promotion : timedeal) {
            runProceedQueue(promotion, events);
        }

        // 프로모션별 대기열의 상태를 업데이트 한다
        for (Long promotion : timedeal) {
            runWaitQueue(promotion, events);
        }

        // 대기열 변화 배치 처리
        runPublish(events);
    }


    /**
     * Redis에 활성화된 프로모션을 조회한다
     */
    private List<Long> getActivePromotion() {
        List<Long> promotions = new ArrayList<>();
        promotions.add(1L);
        return promotions;
    }

    /**
     * 완료 처리
     */
    private void runProceedQueue(Long timedealId, List<QueueStatusEvent> events) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 dealId로 구분
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // 진행 큐에서 만료된 유저 정리
        long now = System.currentTimeMillis();
        zSetOps.removeRangeByScore(proceedQueueKey, 0, now);

        // wait queue에서 10명씩 처리
        Set<String> users = zSetOps.range(waitQueueKey, 0, 9);
        if (users == null || users.isEmpty()) {
            return;
        }

        // 대기열을 통과한 사용자를 진행 큐에 5분 동안 추가한다
        for (String userStr : users) {
            if (zSetOps.rank(proceedQueueKey, userStr) == null) {

                long userId = Long.parseLong(userStr.split(":")[1]);
                long expireAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
                zSetOps.add(proceedQueueKey, userStr, expireAt);

                QueueStatusEvent event = new QueueStatusEvent(
                        userId,
                        timedealId,
                        0L,
                        0L,
                        0L,
                        QueueStatus.PROCEED
                );
                events.add(event);
            }

            zSetOps.remove(waitQueueKey, userStr);
        }
    }

    /**
     * 대기열 처리
     */
    private void runWaitQueue(Long timedealId, List<QueueStatusEvent> events) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 dealId로 구분
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        // 대기열에 있는 유저 모두 출력
        Set<String> users = zSetOps.range(waitQueueKey, 0, -1);

        long position = 1;
        long total = users.size();
        for (String userStr : users) {
            long userId = Long.parseLong(userStr.split(":")[1]);

            QueueStatusEvent event = new QueueStatusEvent(
                    userId,
                    timedealId,
                    position,
                    total,
                    (position / 10) * 10,
                    QueueStatus.WAITING
            );
            position++;

            events.add(event);
        }
    }
    /**
     * 배치처리하여 전송
     */
    private void runPublish(List<QueueStatusEvent> events) {
        publisher.publishBatch(events);
    }
}
