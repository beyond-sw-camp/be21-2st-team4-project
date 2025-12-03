package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.QueueResponse;
import com.ohgiraffers.timedeal.core.enums.QueueStatus;
import com.ohgiraffers.timedeal.core.messaging.SseEmitterRegistry;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.core.support.key.TimedealKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SseEmitterRegistry sseEmitterRegistry;

    public QueueResponse enterQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        try {
            // proceed queue에 있는지 확인
            Double expireAt = zSetOps.score(proceedQueueKey, userStr);
            if (expireAt != null && expireAt > System.currentTimeMillis()) {
                return new QueueResponse(0L, 0L, QueueStatus.PROCEED);
            }

            // 현재 시간을 UTC기준 Millisecond로 변환하여 score로 사용
            long score = System.currentTimeMillis();

            // 신규유저는 생성되고 기존유저는 업데이트됨
            zSetOps.add(waitQueueKey, userStr, score);

            // User의 Queue상태를 저장
            Long position = zSetOps.rank(waitQueueKey, userStr);
            Long waitTime = getWaitTime(position);
            return new QueueResponse(position + 1, waitTime, QueueStatus.WAITING);

        } catch (RedisConnectionFailureException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        }
    }

    public QueueResponse getQueueStatus(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        try {
            // wait queue에 있는지 확인
            Long position = zSetOps.rank(waitQueueKey, userStr);
            if (position != null) {
                Long waitTime = getWaitTime(position);
                return new QueueResponse(position + 1, waitTime, QueueStatus.WAITING);
            }

            // proceed queue에 있는지 확인
            Double expireAt = zSetOps.score(proceedQueueKey, userStr);
            if (expireAt != null && expireAt > System.currentTimeMillis()) {
                return new QueueResponse(0L, 0L, QueueStatus.PROCEED);
            }

            // 없다면 만료되었거나 시도된적이 없음
            return new QueueResponse(0L, 0L, QueueStatus.EXPIRED);

        } catch (RedisConnectionFailureException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        }
    }

    public Boolean leaveQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // 유저를 특정 대기열에서 삭제
        try {
            Long waitRemoved = zSetOps.remove(waitQueueKey, userStr);
            Long proceedRemoved = zSetOps.remove(proceedQueueKey, userStr);
            return (waitRemoved != null && waitRemoved > 0) || (proceedRemoved != null && proceedRemoved > 0);

        } catch (RedisConnectionFailureException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        }
    }

    public void verifyQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // key값을 timedealId로 구분
        String userStr = "user:" + userId;
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        // proceed queue에 있는지 확인
        try {
            Double expireAt = zSetOps.score(proceedQueueKey, userStr);
            if (expireAt == null) {
                throw new CoreException(ErrorType.QUEUE_NOT_FOUND);
            }

            // 기간만료인지 확인
            if (expireAt < System.currentTimeMillis()) {
                throw new CoreException(ErrorType.QUEUE_EXPIRED);
            }
        } catch (RedisConnectionFailureException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        }
    }

    public SseEmitter getQueueSubscribe(Long userId) {
        return sseEmitterRegistry.createEmitter(userId);
    }

    private Long getWaitTime(Long position) {
        return (position / 10) * 10;
    }
}
