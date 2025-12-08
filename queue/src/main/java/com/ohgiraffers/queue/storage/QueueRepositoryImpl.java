package com.ohgiraffers.queue.storage;

import com.ohgiraffers.common.constants.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 유저가 타임딜 대기열에 존재하는지 확인
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 유저가 대기열에 존재한다면 true
     */
    @Override
    public boolean isUserValidInWaitQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        Long position = zSetOps.rank(waitQueueKey, userStr);
        return position != null;
    }

    /**
     * 유저의 타임딜 대기열 순번 조회
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 순번 (1부터 시작), 대기열에 없으면 empty
     */
    @Override
    public Optional<Long> getWaitQueuePosition(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        Long position = zSetOps.rank(waitQueueKey, userStr);
        if (position == null) {
            return Optional.empty();
        }

        return Optional.of(position + 1);
    }

    /**
     * 유저의 타임딜 진행큐 유효시간 조회
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 유효시간 (밀리초), 진행큐에 없으면 empty
     */
    @Override
    public Optional<Double> getProceedQueueExpire(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        return Optional.ofNullable(zSetOps.score(proceedQueueKey, userStr));
    }

    /**
     * 유저가 타임딜 진행큐에 유효하며 존재하는지 확인
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 유저가 진행큐에 존재하고 유효기간 안이라면 true 반환
     */
    @Override
    public boolean isUserValidInProceedQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        Double expireAt = zSetOps.score(proceedQueueKey, userStr);
        return expireAt != null && expireAt >= System.currentTimeMillis();
    }

    /**
     * 대기열 전체 유저 조회
     *
     * @param timedealId 타임딜 ID
     * @return 대기열 전체 유저 셋
     */
    @Override
    public Set<String> getAllWaitQueue(Long timedealId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        return zSetOps.range(waitQueueKey, 0, -1);
    }

    /**
     * 대기열 카운트 조회
     *
     * @param timedealId 타임딜 ID
     * @return 대기열의 카운트
     */
    @Override
    public Optional<Long> getWaitQueueCount(Long timedealId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String waitQueueKey = TimedealKeys.waitQueue(timedealId);
        return Optional.ofNullable(zSetOps.size(waitQueueKey));
    }

    /**
     * 진행큐 카운트 조회
     *
     * @param timedealId 타임딜 ID
     * @return 진행큐의 카운트
     */
    @Override
    public Optional<Long> getProceedQueueCount(Long timedealId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);
        return Optional.ofNullable(zSetOps.size(proceedQueueKey));
    }

    /**
     * 유저를 현재 시간을 기준으로 대기열에 추가
     * 신규유저의 경우 새로 추가됨
     * 기존유저의 경우 업데이트 됨
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 추가 유무
     */
    @Override
    public boolean addWaitQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        long score = System.currentTimeMillis();
        Boolean added = zSetOps.add(waitQueueKey, userStr, score);
        return added != null && added;
    }

    /**
     * 존재하지 않는다면 추가
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 추가 유무
     */
    @Override
    public boolean addWaitQueueIfAbsent(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        long score = System.currentTimeMillis();
        Boolean added = zSetOps.addIfAbsent(waitQueueKey, userStr, score);
        return added != null && added;
    }

    /**
     * 유저를 TTL(5분)을 설정하여 진행큐에 추가
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     * @return 추가 유무
     */
    @Override
    public boolean addProceedQueue(Long timedealId, Long userId, long expireAt) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        Boolean added = zSetOps.add(proceedQueueKey, userStr, expireAt);
        return added != null && added;
    }

    /**
     * 유저를 대기열에서 제거
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     */
    @Override
    public boolean removeWaitQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        Long waitRemoved = zSetOps.remove(waitQueueKey, userStr);
        return (waitRemoved != null && waitRemoved > 0);
    }

    /**
     * 대기열의 카운트에 따른 범위 제거
     *
     * @param timedealId 타임딜 ID
     * @param count      삭제할 카운트
     * @return 삭제된 유저 셋
     */
    @Override
    public Set<String> removeRangeWaitQueue(Long timedealId, Long count) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        String waitQueueKey = TimedealKeys.waitQueue(timedealId);

        Set<String> users = zSetOps.range(waitQueueKey, 0, count - 1);
        if (users != null && !users.isEmpty()) {
            zSetOps.removeRange(waitQueueKey, 0, count - 1);
        }
        return users;
    }

    /**
     * 유저를 진행큐에서 제거
     *
     * @param timedealId 타임딜 ID
     * @param userId     유저 ID
     */
    @Override
    public boolean removeProceedQueue(Long timedealId, Long userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String userStr = TimedealKeys.makeUser(userId);
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        Long proceedRemoved = zSetOps.remove(proceedQueueKey, userStr);
        return (proceedRemoved != null && proceedRemoved > 0);
    }

    /**
     * 진행큐의 유효기간에 따른 범위 삭제
     *
     * @param timedealId 타임딜 ID
     * @param now        현재 타임 스탬프
     */
    @Override
    public Set<String> removeRangeProceedQueue(Long timedealId, Long now) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        String proceedQueueKey = TimedealKeys.proceedQueue(timedealId);

        Set<String> users = zSetOps.range(proceedQueueKey, 0, now);
        if (users != null && !users.isEmpty()) {
            zSetOps.removeRange(proceedQueueKey, 0, now);
        }
        return users;
    }
}
