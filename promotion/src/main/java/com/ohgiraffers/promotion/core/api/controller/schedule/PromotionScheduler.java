package com.ohgiraffers.promotion.core.api.controller.schedule;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.promotion.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.domain.PromotionService;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.ohgiraffers.promotion.core.enums.PromotionStatus.*;

@Component
@RequiredArgsConstructor
public class PromotionScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final PromotionService promotionService;
    @Scheduled(fixedRate = 60000)
    public void updateSchedule() {
        List<Promotion> checkSchedule = promotionService.updateStatus();
        for(Promotion promotion : checkSchedule){
            if(promotion.getPromotionStatus() == null){
                promotion.setPromotionStatus(SCHEDULER);
            }
            promotionService.changePromotionStatus(promotion.getId());
        }
    }

    @Scheduled(fixedRate = 60000)
    public void updatePromotionSchedule() {
        List<Promotion> checkSchedule = promotionService.updateStatus();
        for(Promotion promotion : checkSchedule){
            if(promotion.getPromotionStatus() == null){
                promotionService.updatePromotionStatus(promotion.getId(), SCHEDULER);
            }
            if(promotion.getPromotionStatus() == SCHEDULER && promotion.getStartTime().isBefore(LocalDateTime.now())){
                promotionService.updatePromotionStatus(promotion.getId(), ACTIVE);
            } else if (promotion.getPromotionStatus() == ACTIVE && promotion.getEndTime().isBefore(LocalDateTime.now())) {
                promotionService.updatePromotionStatus(promotion.getId(),ENDED);

            }

        }
    }


    @Scheduled(fixedRate = 60000)
    public void checkpromotion() {
        List<RedisPromotionResponse> schedulePromotion =  promotionService.returnSchedule(SCHEDULER);
        List<RedisPromotionResponse> activePromotion = promotionService.returnActive(SCHEDULER);
        for(RedisPromotionResponse promotion : schedulePromotion){
            String key = TimedealKeys.setPromotion(promotion.id());
            Integer value = promotion.totalQuantity();
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
        }
        for(RedisPromotionResponse promotion : activePromotion){
            String key = TimedealKeys.setPromotion(promotion.id());
            stringRedisTemplate.delete(key);
        }
    }


}
