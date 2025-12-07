package com.ohgiraffers.queue.core.api.controller.scheduler;

import com.ohgiraffers.common.constants.QueueConstants;
import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.queue.core.api.controller.v1.message.QueueStatusEvent;
import com.ohgiraffers.queue.core.domain.WaitTimeEstimator;
import com.ohgiraffers.queue.core.enums.QueueStatus;
import com.ohgiraffers.queue.core.messaging.QueueStatusPublisher;
import com.ohgiraffers.queue.core.storage.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final RedissonClient redissonClient;
    private final QueueStatusPublisher publisher;
    private final WaitTimeEstimator waitTimeEstimator;
    private final QueueRepository queueRepository;

    /**
     * 대기열 처리를 10초마다 처리한다
     */
    @Scheduled(fixedRate = QueueConstants.QUEUE_SCHEDULER_INTERVAL_MILLIS)
    public void scheduled() {

        RLock lock = redissonClient.getLock("queue-scheduler-lock");
        if (!lock.tryLock()) {
            return;
        }

        try {
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

        } finally {
            lock.unlock();
        }
    }


    /**
     * Redis에 활성화된 프로모션을 조회한다
     *
     * @return 활성화된 프로모션 리스트
     */
    private List<Long> getActivePromotion() {

        // 활성화되어 있는 프로모션 조회

        List<Long> promotions = new ArrayList<>();
        promotions.add(1L);
        return promotions;
    }

    /**
     * 진행큐에 수용가능하다면 대기열에서 제거하여 이동
     * 이동했다면 상태 전송하기
     *
     * @param timedealId 타임딜 ID
     * @param events     진행큐 상태 저장
     */
    private void runProceedQueue(Long timedealId, List<QueueStatusEvent> events) {

        // 진행 큐에서 만료된 유저 정리
        Set<String> expiredUsers = queueRepository.removeRangeProceedQueue(timedealId, System.currentTimeMillis());
        for (String userStr : expiredUsers) {
            long userId = Long.parseLong(userStr);
            events.add(QueueStatusEvent.of(userId, timedealId, QueueStatus.EXPIRED));
        }

        // proceed queue의 크기 구하기
        Optional<Long> count = queueRepository.getProceedQueueCount(timedealId);
        if (count.isEmpty()) {
            return;
        }

        // 가용할 수 있는 인원 구하기
        long available = QueueConstants.PROCEED_QUEUE_CAPACITY - count.get();
        if (available <= 0) {
            return;
        }

        Set<String> users = queueRepository.removeRangeWaitQueue(timedealId, available);
        if (users == null || users.isEmpty()) {
            return;
        }

        // 대기열을 통과한 사용자를 진행 큐에 5분 동안 추가한다
        for (String userStr : users) {
            long userId = TimedealKeys.getUser(userStr);
            if (!queueRepository.isUserValidInProceedQueue(timedealId, userId)) {

                long expireAt = System.currentTimeMillis() + QueueConstants.PROCEED_QUEUE_TTL_MILLIS;
                queueRepository.addProceedQueue(timedealId, userId, expireAt);
                events.add(QueueStatusEvent.of(userId, timedealId, QueueStatus.PROCEED));
            }
        }
    }

    /**
     * 대기열에 있는 유저의 상태를 갱신하여 저장
     *
     * @param timedealId 타임딜 ID
     * @param events     대기열 상태 저장
     */
    private void runWaitQueue(Long timedealId, List<QueueStatusEvent> events) {

        // 대기열에 있는 유저 모두 출력
        Set<String> users = queueRepository.getAllWaitQueue(timedealId);
        if (users == null || users.isEmpty()) {
            return;
        }

        long position = 1;
        long total = users.size();
        for (String userStr : users) {
            long userId = TimedealKeys.getUser(userStr);

            QueueStatusEvent event = new QueueStatusEvent(
                    userId,
                    timedealId,
                    position,
                    total,
                    waitTimeEstimator.estimateWaitSeconds(timedealId, position),
                    QueueStatus.WAITING
            );
            position++;

            events.add(event);
        }
    }

    /**
     * 대기열 상태를 배치로 발행
     *
     * @param events 대기열 상태 리스트
     */
    private void runPublish(List<QueueStatusEvent> events) {
        publisher.publishBatch(events);
    }
}
