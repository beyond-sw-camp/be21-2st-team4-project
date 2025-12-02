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
    PromotionService promotionService;
    @Scheduled(fixedRate = 60000)
    public void checkpromotion() {
        List<RedisPromotionResponse> schedulePromotion = promotionService.returnSchedule();
        List<RedisPromotionResponse> activePromotion = promotionService.returnActive();
        String setPromtion = TimedealKeys.setPromotion(schedulePromotion.get(0));
        String deletePromtion = TimedealKeys.deletePromotion(activePromotion.get(0));
        switch(promotion.getPromotionStatus()) {
            case SCHEDULER :
                if(promotion.getStartTime().isAfter(LocalDateTime.now())){
                    promotion.changeStatus(PromotionStatus.ACTIVE);
                    savePromotion(promotion.getId(),promotion.getTotalQuantity());
                }
                break;
            case ACTIVE :
                if(promotion.getEndTime().isAfter(LocalDateTime.now())){
                    promotion.changeStatus(PromotionStatus.ENDED);
                    deletePromotion(promotion.getId());
                }
                break;
        }
    }

    public void savePromotion(Long timedealId, int totalQuantity) {
        String key = TimedealKeys.setPromotion(timedealId);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(totalQuantity));
    }

    public void deletePromotion(Long timedealId) {
        String key = TimedealKeys.deletePromotion(timedealId);
        stringRedisTemplate.delete(key);
    }

}
