package com.ohgiraffers.timedeal.core.api.controller.scheduler;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.domain.PromotionService;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.key.TimedealKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final PromotionService promotionService;
    @Scheduled(fixedRate = 60000)
    public void checkpromotion() {
        List<RedisPromotionResponse> schedulePromotion =  promotionService.returnSchedule(PromotionStatus.SCHEDULER);
        List<RedisPromotionResponse> activePromotion = promotionService.returnActive(PromotionStatus.SCHEDULER);
        for(RedisPromotionResponse promotion : schedulePromotion){
            String key = TimedealKeys.setPromotion(promotion.timedealId());
            Integer value = promotion.totalQuantity();
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
        }
        for(RedisPromotionResponse promotion : activePromotion){
            String key = TimedealKeys.setPromotion(promotion.timedealId());
            stringRedisTemplate.delete(key);
        }
    }

}
