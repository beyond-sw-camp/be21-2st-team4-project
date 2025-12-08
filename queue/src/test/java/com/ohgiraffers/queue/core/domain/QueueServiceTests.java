package com.ohgiraffers.queue.core.domain;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.queue.core.api.controller.v1.response.QueueStatusResponse;
import com.ohgiraffers.queue.core.enums.QueueStatus;
import com.ohgiraffers.queue.core.messaging.SseEmitterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("대기열 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class QueueServiceTests {

    @Mock
    StringRedisTemplate stringRedisTemplate;

    @Mock
    ZSetOperations<String, String> zSetOps;

    @Mock
    SseEmitterRegistry sseEmitterRegistry;

    @InjectMocks
    QueueService queueService;

    @Test
    @DisplayName("첫 진입 시 순번 0을 반환")
    public void firstEnterQueue() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.add(anyString(), any(), anyDouble())).willReturn(true);
        given(zSetOps.rank(anyString(), any())).willReturn(0L);

        // when
        QueueStatusResponse response = queueService.enterQueue(dealId, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.position()).isEqualTo(1L);
        assertThat(response.waitingTime()).isEqualTo(0L);
        assertThat(response.status()).isEqualTo(QueueStatus.WAITING);

        verify(zSetOps).add(anyString(), any(), anyDouble());
        verify(zSetOps).rank(anyString(), any());
    }

    @Test
    @DisplayName("10번째 진입 시 대기시간 100초를 반환한다")
    public void lateEnterQueue() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.add(anyString(), any(), anyDouble())).willReturn(true);
        given(zSetOps.rank(anyString(), any())).willReturn(10L);

        // when
        QueueStatusResponse response = queueService.enterQueue(dealId, userId);

        // then
        assertThat(response.position()).isEqualTo(11L);
        assertThat(response.waitingTime()).isEqualTo(queueService.getWaitingTime(dealId, response.position()));
    }

    @Test
    @DisplayName("중복 진입 시 기존 score 업데이트")
    void enterQueueDuplicate() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.add(anyString(), any(), anyDouble())).willReturn(false);
        given(zSetOps.rank(anyString(), any())).willReturn(3L);

        // when
        QueueStatusResponse response = queueService.enterQueue(dealId, userId);

        // then
        assertThat(response.position()).isEqualTo(3L);
        verify(zSetOps).add(eq("queue:1"), eq(TimedealKeys.makeUser(userId)), anyDouble());
    }

    @Test
    @DisplayName("dealId별로 독립적인 대기열이 생성")
    void separateQueueByDealId() {
        // given
        Long dealId1 = 1L;
        Long dealId2 = 2L;
        Long userId = 100L;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.add(anyString(), any(), anyDouble())).willReturn(true);
        given(zSetOps.rank(anyString(), any())).willReturn(0L);

        // when
        queueService.enterQueue(dealId1, userId);
        queueService.enterQueue(dealId2, userId);

        // then
        verify(zSetOps).add(eq("queue:1"), eq(TimedealKeys.makeUser(userId)), anyDouble());
        verify(zSetOps).add(eq("queue:2"), eq(TimedealKeys.makeUser(userId)), anyDouble());
    }

    @Test
    @DisplayName("대기열에 있으면 WAITING 반환")
    void waitingQueue() {
        // given
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.rank(anyString(), anyString())).willReturn(5L);

        // when
        QueueStatusResponse response = queueService.getQueueStatus(1L, 100L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    @DisplayName("진행큐에 있고 만료되지 않았다면 READY 반환")
    void ProceedQueue() {
        // given
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.rank(anyString(), anyString())).willReturn(null);
        given(zSetOps.score(anyString(), anyString())).willReturn((double) System.currentTimeMillis() + 60000);

        // when
        QueueStatusResponse response = queueService.getQueueStatus(1L, 100L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(QueueStatus.PROCEED);
    }

    @Test
    @DisplayName("대기열 및 진행큐에 없거나 만료되었다면 EXPIRED 반환")
    void expiredQueue() {
        // given
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.rank(anyString(), anyString())).willReturn(null);
        given(zSetOps.score(anyString(), anyString())).willReturn(null);

        // when
        QueueStatusResponse response = queueService.getQueueStatus(1L, 100L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(QueueStatus.EXPIRED);
    }

    @Test
    @DisplayName("대기열에 진입 후 나간다면 대기열에서 삭제")
    void leaveWaitQueue() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(dealId);

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.add(anyString(), any(), anyDouble())).willReturn(true);
        given(zSetOps.rank(anyString(), any())).willReturn(0L);
        given(zSetOps.remove(anyString(), any())).willReturn(1L);

        // when
        queueService.enterQueue(dealId, userId);
        queueService.leaveQueue(dealId, userId);

        // then
        verify(zSetOps).remove(eq(waitQueueKey), eq(userStr));
    }

    @Test
    @DisplayName("특정 진행큐에 존재하는지 확인")
    void verifyQueue() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(dealId);

        double expireAt = System.currentTimeMillis() + 5 * 60 * 1000;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.score(proceedQueueKey, userStr)).willReturn(expireAt);

        // when & then
        assertThatCode(() -> queueService.verifyQueue(dealId, userId)).doesNotThrowAnyException();

    }

    @Test
    @DisplayName("진행큐에 존재하지 않으면 예외 발생")
    void verifyQueue_NotExist() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(dealId);

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.score(proceedQueueKey, userStr)).willReturn(null);

        // when & then
        assertThatCode(() -> queueService.verifyQueue(dealId, userId)).isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("진행큐에 존재는 헀지만 만료시간이 끝난경우 실패")
    void verifyQueue_Expire() {
        // given
        Long dealId = 1L;
        Long userId = 100L;

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(dealId);

        double expireAt = System.currentTimeMillis() - 5 * 60 * 1000;

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOps);
        given(zSetOps.score(proceedQueueKey, userStr)).willReturn(expireAt);

        // when & then
        assertThatCode(() -> queueService.verifyQueue(dealId, userId)).isInstanceOf(CoreException.class);
    }

    @Test
    void simpleTest() {
        assertTrue(true);
    }
}