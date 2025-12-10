package com.ohgiraffers.promotion.core.api.controller.schedule;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionListResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.RedisPromotionResponse;
import static com.ohgiraffers.promotion.core.enums.PromotionStatus.*;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.domain.PromotionService;
import com.ohgiraffers.promotion.storage.PromotionRepository;
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
    private final PromotionRepository promotionRepository;

    @Scheduled(fixedRate = 5000)
    public void updatePromotionSchedule() {
        LocalDateTime now = LocalDateTime.now();

        List<Promotion> toActivate = promotionRepository.findByPromotionStatusAndStartTimeBefore(SCHEDULER, now);
        for (Promotion promotion : toActivate) {
            promotionService.updatePromotionStatus(promotion.getId(), ACTIVE);

            String key = TimedealKeys.setPromotion(promotion.getId());
            stringRedisTemplate.opsForValue().set(key, promotion.getTotalQuantity().toString());
        }

        List<Promotion> toEnd = promotionRepository.findByPromotionStatusAndEndTimeBefore(ACTIVE, now);
        for (Promotion promotion : toEnd) {
            promotionService.updatePromotionStatus(promotion.getId(), ENDED);

            String key = TimedealKeys.setPromotion(promotion.getId());
            stringRedisTemplate.delete(key);
        }
    }

    @Scheduled(fixedRate = 100000)
    public void checkQuantity(){
//        List<Promotion> checkSchedule = promotionService.updateStatus();
//
//        for(Promotion promotion : checkSchedule){
//            if(promotion.getTotalQuantity() != (promotion.getSoldQuantity() + (Long.parseLong(stringRedisTemplate.opsForValue().get(promotion.getId()))))){
//                String key = TimedealKeys.setPromotion(promotion.getId());
//                Integer value = promotion.getTotalQuantity() - promotion.getSoldQuantity();
//                stringRedisTemplate.opsForValue().set(key,String.valueOf(value));
//            }
//        }
    }
}
