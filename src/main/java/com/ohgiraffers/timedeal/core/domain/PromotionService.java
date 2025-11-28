package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import com.ohgiraffers.timedeal.storage.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ohgiraffers.timedeal.core.support.response.ApiResult.success;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public Promotion promotionUpdateStatusById(Long id) {
        promotionRepository.promotionUpdateStatusById(id)
    }

    @Autowired
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void findByProductId(Long productId) {
        promotionRepository.findByProductId(productId);
    }

        public PromotionResponse promotionSave(PromotionRequest pr) {
            AtomicBoolean created = new AtomicBoolean(false);

            Promotion promotion =
                    promotionRepository.findByProductId(pr.getProductID())
                            .filter(p -> p.getPromotionStatus() != PromotionStatus.ENDED)
                            .orElseGet(() -> {
                               Promotion promotion1 = new Promotion(
                                       pr.getAdminId(),
                                       pr.getProductID(),
                                       pr.getDiscountRate(),
                                       pr.getStartTime(),
                                       pr.getEndTime(),
                                       pr.getTotalQuantity()
                               );
                               created.set(true);
                               return promotionRepository.save(promotion1);

                            });
            return new PromotionResponse(created.get());
        }

}
/*
PromotionStatus status = promotion.getPromotionStatus();
        switch (status) {
        case SCHEDULER:
        if(promotion.getStartTime().isAfter(LocalDateTime.now())){
        promotion.setPromotionStatus(PromotionStatus.ACTIVE);
                }
                        break;
                        case ACTIVE:
        if(promotion.getEndTime().isAfter(LocalDateTime.now())){
        promotion.setPromotionStatus(PromotionStatus.ENDED);
                }
                        break;
default:break;
        }*/
