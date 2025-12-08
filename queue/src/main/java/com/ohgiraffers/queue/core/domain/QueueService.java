package com.ohgiraffers.queue.core.domain;

import com.ohgiraffers.common.constants.QueueConstants;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.queue.core.api.command.CommandClient;
import com.ohgiraffers.queue.core.api.controller.v1.response.QueueStatusResponse;
import com.ohgiraffers.queue.core.enums.QueueStatus;
import com.ohgiraffers.queue.core.messaging.SseEmitterRegistry;
import com.ohgiraffers.queue.storage.QueueRepository;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final WaitTimeEstimator waitTimeEstimator;
    private final QueueRepository queueRepository;
    private final CommandClient commandClient;

    /**
     * 신규유저 또는 기존유저 대기열 진입
     * 새로운 유저라면 추가, 기존 유저라면 업데이트
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     */
    public QueueStatusResponse enterQueue(Long timedealId, Long userId) {
        try {
            // 유저가 존재하는지 확인
            if(commandClient.isValidUser(userId)) {
                throw new CoreException(ErrorType.USER_NOT_FOUND);
            }
             
            // 프로모션이 존재하는지 확인
            if(commandClient.isValidPromotion(timedealId)) {
                throw new CoreException(ErrorType.QUEUE_NOT_FOUND);
            }

            // 유저를 대기열에 추가 (기존 유저를 초기화 할것인지?)
            //boolean added = queueRepository.addWaitQueueIfAbsent(timedealId, userId);
            boolean added = queueRepository.addWaitQueue(timedealId, userId);

            // 특정 유저가 타임딜 진행큐에 유효하며 존재하는지 확인
            if (queueRepository.isUserValidInProceedQueue(timedealId, userId)) {
                if (added) {
                    queueRepository.removeWaitQueue(timedealId, userId);
                }
                return QueueStatusResponse.of(QueueStatus.PROCEED);
            }

            // User의 Queue상태를 저장
            Long position = queueRepository.getWaitQueuePosition(timedealId, userId).orElseThrow(() -> new CoreException(ErrorType.QUEUE_NOT_FOUND));
            Long totalWaiting = queueRepository.getWaitQueueCount(timedealId).orElseThrow(() -> new CoreException(ErrorType.QUEUE_NOT_FOUND));
            Long waitingTime = waitTimeEstimator.estimateWaitSeconds(timedealId, position);
            return new QueueStatusResponse(position, totalWaiting, waitingTime, QueueStatus.WAITING);

        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        } catch (RedisException e) {
            throw new CoreException(ErrorType.REDIS_ERROR);
        } catch (RuntimeException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    /**
     * 대기열 및 잰행큐의 상태 조회
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     */
    public QueueStatusResponse getQueueStatus(Long timedealId, Long userId) {
        try {
            // wait queue에 있는지 확인
            Optional<Long> position = queueRepository.getWaitQueuePosition(timedealId, userId);
            if (position.isPresent()) {
                Long totalWaiting = queueRepository.getWaitQueueCount(timedealId).orElseThrow(() -> new CoreException(ErrorType.QUEUE_NOT_FOUND));
                long waitingTime = waitTimeEstimator.estimateWaitSeconds(timedealId, position.get());
                return new QueueStatusResponse(position.get(), totalWaiting, waitingTime, QueueStatus.WAITING);
            }

            // proceed queue에 있는지 확인
            if (queueRepository.isUserValidInProceedQueue(timedealId, userId)) {
                return QueueStatusResponse.of(QueueStatus.PROCEED);
            }

            // 없다면 만료되었거나 시도된적이 없음
            return QueueStatusResponse.of(QueueStatus.EXPIRED);

        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        } catch (RedisException e) {
            throw new CoreException(ErrorType.REDIS_ERROR);
        }
    }

    /**
     * 대기열 및 잰행큐에서 나가기
     * 진행큐에 존재했다면 처리 시간은 기록
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 대기열 또는 진행큐에서 삭제되었다면 true
     */
    public Boolean leaveQueue(Long timedealId, Long userId) {
        try {

            Optional<Double> expireAt = queueRepository.getProceedQueueExpire(timedealId, userId);
            if (expireAt.isPresent()) {
                long remainingTimeMs = expireAt.get().longValue() - System.currentTimeMillis();
                long usedTimeMs = QueueConstants.PROCEED_QUEUE_TTL_MILLIS - remainingTimeMs;
                waitTimeEstimator.recordProcessingTime(timedealId, usedTimeMs);
            }

            boolean waitRemoved = queueRepository.removeWaitQueue(timedealId, userId);
            boolean proceedRemoved = queueRepository.removeProceedQueue(timedealId, userId);
            return (waitRemoved || proceedRemoved);

        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        } catch (RedisException e) {
            throw new CoreException(ErrorType.REDIS_ERROR);
        }
    }

    /**
     * 유저가 타임딜 진행큐에 유효하며 존재하는지 확인
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     */
    public void verifyQueue(Long timedealId, Long userId) {
        try {
            boolean isVerify = queueRepository.isUserValidInProceedQueue(timedealId, userId);
            if (!isVerify) {
                throw new CoreException(ErrorType.QUEUE_NOT_FOUND);
            }

        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            throw new CoreException(ErrorType.REDIS_CONN_FAILURE);
        } catch (RedisException e) {
            throw new CoreException(ErrorType.REDIS_ERROR);
        }
    }

    /**
     * SSE 생성 및 반환
     *
     * @param userId 유저 ID
     * @return SSE 반환
     */
    public SseEmitter getQueueSubscribe(Long userId) {
        return sseEmitterRegistry.createEmitter(userId);
    }

}
