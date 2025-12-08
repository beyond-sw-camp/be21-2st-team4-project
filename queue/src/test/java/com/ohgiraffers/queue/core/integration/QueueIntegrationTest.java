package com.ohgiraffers.queue.core.integration;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.queue.core.api.controller.scheduler.QueueScheduler;
import com.ohgiraffers.queue.core.api.controller.v1.response.QueueStatusResponse;
import com.ohgiraffers.queue.core.domain.QueueService;
import com.ohgiraffers.queue.core.enums.QueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("대기열 통합 테스트")
public class QueueIntegrationTest {

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueScheduler queueScheduler;

    @BeforeEach
    void setUpBase() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushDb();
    }

    @Nested
    @DisplayName("대기열 전체 플로우")
    class QueueFlow {

        @Test
        @DisplayName("100명이 동시에 진입하면 순번이 0~99까지 할당")
        void concurrentEnterQueue() {
            // given
            Long dealId = 1L;
            int userCount = 100;
            List<QueueStatusResponse> responses = new ArrayList<>();

            // when
            for (long userId = 1; userId <= userCount; userId++) {
                QueueStatusResponse response = queueService.enterQueue(dealId, userId);
                responses.add(response);
            }

            // then
            assertThat(responses).hasSize(userCount);

            // Redis에 100명이 저장되었는지 확인
            ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
            Long size = zSetOps.size(TimedealKeys.waitQueue(1L));
            assertThat(size).isEqualTo(userCount);

            // 모든 응답이 유효한지 확인
            for (QueueStatusResponse response : responses) {
                assertThat(response.status()).isEqualTo(QueueStatus.WAITING);
                assertThat(response.position()).isBetween(1L, 100L);
            }
        }

        @Test
        @DisplayName("같은 유저가 재진입하면 score가 업데이트되고 순번이 변경")
        void reenterQueue() throws InterruptedException {
            // given
            Long dealId = 1L;
            Long userId = 100L;

            // 다른 유저들 먼저 진입
            for (long i = 1; i <= 10; i++) {
                queueService.enterQueue(dealId, i);
            }

            // when
            QueueStatusResponse firstEnter = queueService.enterQueue(dealId, userId);

            Thread.sleep(100);  // score 차이를 만들기 위해 대기

            QueueStatusResponse secondEnter = queueService.enterQueue(dealId, userId);

            // then
            assertThat(firstEnter.position()).isEqualTo(11L);
            assertThat(secondEnter.position()).isEqualTo(11L);

            Long size = stringRedisTemplate.opsForZSet().size(TimedealKeys.waitQueue(1L));
            assertThat(size).isEqualTo(11L);
        }

        @Test
        @DisplayName("dealId가 다르면 독립적인 대기열이 생성")
        void separateQueueByDealId() {
            // given
            Long dealId1 = 1L;
            Long dealId2 = 2L;
            Long userId = 100L;

            // when
            QueueStatusResponse response1 = queueService.enterQueue(dealId1, userId);
            QueueStatusResponse response2 = queueService.enterQueue(dealId2, userId);

            // then
            assertThat(response1.position()).isEqualTo(1L);
            assertThat(response2.position()).isEqualTo(1L);

            // Redis에 두 개의 독립적인 key가 생성됨
            Set<String> keys = stringRedisTemplate.keys("timedeal:*:wait-queue");
            assertThat(keys).containsExactlyInAnyOrder(TimedealKeys.waitQueue(1L), TimedealKeys.waitQueue(2L));
        }

        @Test
        @DisplayName("대기 순번에 따라 대기시간이 계산")
        void calculateWaitTime() {
            // given
            Long dealId = 1L;

            // when
            List<QueueStatusResponse> responses = new ArrayList<>();
            for (long userId = 1; userId <= 25; userId++) {
                responses.add(queueService.enterQueue(dealId, userId));
            }

            // then
            assertThat(responses.get(0).waitingTime()).isEqualTo(0L);
            assertThat(responses.get(9).waitingTime()).isEqualTo(24L);
            assertThat(responses.get(10).waitingTime()).isEqualTo(27L);
            assertThat(responses.get(24).waitingTime()).isEqualTo(69L);
        }

        @Test
        @DisplayName("대기열에서 진행큐로 이동")
        void waitingQueue() {
            // given
            ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
            String waitQueueKey = TimedealKeys.waitQueue(1L);
            String proceedQueueKey = TimedealKeys.proceedQueue(1L);

            for(long id = 1; id <= 15; id++) {
                String userStr = TimedealKeys.makeUser(id);
                zSetOps.add(waitQueueKey, userStr, System.currentTimeMillis() + id);
            }

            // when
            queueScheduler.scheduled();

            // then
            assertThat(zSetOps.size(waitQueueKey)).isEqualTo(0);
            assertThat(zSetOps.size(proceedQueueKey)).isEqualTo(15);
        }

        @Test
        @DisplayName("만료된 유저 진행큐에서 제거")
        void expiredQueue() {
            // given
            ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
            String proceedQueueKey = TimedealKeys.proceedQueue(1L);

            zSetOps.add(proceedQueueKey, TimedealKeys.makeUser(1L), System.currentTimeMillis() - 1000);
            zSetOps.add(proceedQueueKey, TimedealKeys.makeUser(2L), System.currentTimeMillis() + 300000);

            // when
            queueScheduler.scheduled();

            // then
            assertThat(zSetOps.score(proceedQueueKey, TimedealKeys.makeUser(1L))).isNull();
            assertThat(zSetOps.score(proceedQueueKey, TimedealKeys.makeUser(2L))).isNotNull();
        }

        @Test
        @DisplayName("Redis flush 후 대기열이 초기화")
        void flushQueue() {
            // given
            Long dealId = 1L;
            queueService.enterQueue(dealId, 100L);
            queueService.enterQueue(dealId, 200L);

            // when
            stringRedisTemplate.getConnectionFactory()
                    .getConnection()
                    .serverCommands()
                    .flushDb();

            // then
            Set<String> keys = stringRedisTemplate.keys("timedeal:*:wait-queue");
            assertThat(keys).isEmpty();
        }
    }
}
