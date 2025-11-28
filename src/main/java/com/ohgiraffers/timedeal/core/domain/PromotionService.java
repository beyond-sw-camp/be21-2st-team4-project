package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import com.ohgiraffers.timedeal.core.support.response.ResultType;
import com.ohgiraffers.timedeal.storage.PromotionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.ohgiraffers.timedeal.core.support.response.ApiResult.success;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void findByProductId(Long productId) {
        promotionRepository.findByProductId(productId);
    }

    public void promotionSave(PromotionRequest pr) {
            AtomicReference<ResultType> createdSuccess = new AtomicReference<>(ResultType.ERROR);

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
                               createdSuccess.set(ResultType.SUCCESS);
                               return promotionRepository.save(promotion1);

                            });
        }

        @Transactional
    public void promotionUpdateStatusById(Long id) {
        promotionRepository.promotionUpdateStatusById(id);
        Promotion promotion = promotionRepository.findById(id).get();

        PromotionStatus status = promotion.getPromotionStatus();
        switch (status) {
            case SCHEDULER:
                if (promotion.getStartTime().isAfter(LocalDateTime.now())) {
                    promotion.changeStatus(PromotionStatus.ACTIVE);
                }
                break;
            case ACTIVE:
                if (promotion.getEndTime().isAfter(LocalDateTime.now())) {
                    promotion.setPromotionStatus(PromotionStatus.ENDED);
                }
                break;
            default:
                break;
        }
    }

    public void deletePromotion(Long Id) {
        int result = promotionRepository.deleteById(Id);
        if (result > 0) {
            //
        }
    }

    public List<PromotionResponse> findAll(){
        return (List<PromotionResponse>) promotionRepository.findAll().stream();

    };


    public List<PromotionResponse> getPromotionsWithStatus(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(promotionStatus);

    }
}

