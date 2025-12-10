package com.ohgiraffers.promotion.core.api.controller.schedule;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionListResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.RedisPromotionResponse;
import static com.ohgiraffers.promotion.core.enums.PromotionStatus.*;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.domain.PromotionService;
import lombok.RequiredArgsConstructor;
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

    @Scheduled(fixedRate = 6000)
    public void updatePromotionSchedule() {
        List<Promotion> checkSchedule = promotionService.updateStatus();
        for(Promotion promotion : checkSchedule){
            if(promotion.getPromotionStatus() == null){
                promotionService.updatePromotionStatus(promotion.getId(), SCHEDULER);
            }
            if(promotion.getPromotionStatus() == SCHEDULER && promotion.getStartTime().isBefore(LocalDateTime.now())){
                promotionService.updatePromotionStatus(promotion.getId(), ACTIVE);
                String key = TimedealKeys.setPromotion(promotion.getId());
                String value = promotion.getTotalQuantity().toString();
                stringRedisTemplate.opsForValue().set(key, value);
            } else if (promotion.getPromotionStatus() == ACTIVE && promotion.getEndTime().isBefore(LocalDateTime.now())) {
                promotionService.updatePromotionStatus(promotion.getId(),ENDED);
                String key = TimedealKeys.setPromotion(promotion.getId());
                stringRedisTemplate.delete(key);

            }

        }
    }

    @Scheduled(fixedRate = 6000)
    public void checkpromotion() {
        List<RedisPromotionResponse> schedulePromotion =  promotionService.returnSchedule(ACTIVE);
        List<RedisPromotionResponse> activePromotion = promotionService.returnSchedule(ENDED);
        for(RedisPromotionResponse promotion : schedulePromotion){
            if(promotionService.findPromotionById(promotion.id()).startTime().isBefore(LocalDateTime.now())) {
                String key = TimedealKeys.setPromotion(promotion.id());
                Integer value = promotion.totalQuantity();
                stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
            }
        }
        for(RedisPromotionResponse promotion : activePromotion){
            if(promotionService.findPromotionById(promotion.id()).endTime().isBefore(LocalDateTime.now())) {
                String key = TimedealKeys.setPromotion(promotion.id());
                stringRedisTemplate.delete(key);
            }
        }
    }

    @Scheduled(fixedRate = 100000)
    public void checkQuantity(){
        List<Promotion> checkSchedule = promotionService.updateStatus();

        for(Promotion promotion : checkSchedule){
            if(promotion.getTotalQuantity() != (promotion.getSoldQuantity() + (Long.parseLong(stringRedisTemplate.opsForValue().get(promotion.getId()))))){
                String key = TimedealKeys.setPromotion(promotion.getId());
                Integer value = promotion.getTotalQuantity() - promotion.getSoldQuantity();
                stringRedisTemplate.opsForValue().set(key,String.valueOf(value));
            }
        }
    }
}
