package com.ohgiraffers.timedeal.core.api.controller.scheduler;

import com.ohgiraffers.timedeal.core.support.key.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final StringRedisTemplate stringRedisTemplate;

    // 고정 간격 실행 (10초)
    @Scheduled(fixedRate = 10000)
    public void runWaitQueue() {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // 현재 등록된 프로모션 timedealId 조회
        Long timedealId = 1L;

        // key값을 dealId로 구분
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // 진행 큐에서 만료된 유저 정리
        long now = System.currentTimeMillis();
        zSetOps.removeRangeByScore(proceedQueueKey, 0, now);

        // wait queue에서 10명씩 처리
        Set<String> users = zSetOps.range(waitQueueKey, 0, 9);
        if(users == null || users.isEmpty()) {
            return;
        }

        // 대기열을 통과한 사용자를 진행 큐에 5분 동안 추가한다
        for(String userStr : users) {
            if(zSetOps.rank(proceedQueueKey, userStr) != null) {
                continue;
            }

            long expireAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
            zSetOps.add(proceedQueueKey, userStr, expireAt);
        }
        zSetOps.removeRange(waitQueueKey, 0, users.size() - 1);
    }
}
