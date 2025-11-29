package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.QueueResponse;
import com.ohgiraffers.timedeal.core.enums.QueueStatus;
import com.ohgiraffers.timedeal.core.support.key.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate stringRedisTemplate;

    public QueueResponse enterQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // proceed queue에 있는지 확인
        if(zSetOps.rank(proceedQueueKey, userStr) != null) {
            return new QueueResponse(0L, 0L, QueueStatus.PROCEED);
        }

        // 현재 시간을 UTC기준 Millisecond로 변환하여 score로 사용
        long score = System.currentTimeMillis();
        
        // 신규유저는 생성되고 기존유저는 업데이트됨
        zSetOps.add(waitQueueKey, userStr, score);

        // User의 Queue상태를 저장
        Long position = zSetOps.rank(waitQueueKey, userStr);
        Long waitTime = getWaitTime(position);
        return new QueueResponse(position, waitTime, QueueStatus.WAITING);
    }

    public QueueResponse getQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // wait queue에 있는지 확인
        Long position = zSetOps.rank(waitQueueKey, userStr);
        if(position != null) {
            Long waitTime = getWaitTime(position);
            return new QueueResponse(position, waitTime, QueueStatus.WAITING);
        }

        // proceed queue에 있는지 확인
        Double expireAt = zSetOps.score(proceedQueueKey, userStr);
        if(expireAt != null && expireAt > System.currentTimeMillis()) {
            return new QueueResponse(0L, 0L, QueueStatus.PROCEED);
        }
        
        // 없다면 만료되었거나 시도된적이 없음
        return new QueueResponse(0L, 0L, QueueStatus.EXPIRED);
    }

    private Long getWaitTime(Long position) {
        return (position / 10) * 10;
    }
}
