package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.RedisPromotionResponse;
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

    public void promotionSave(PromotionRequest pr) {
            AtomicReference<ResultType> createdSuccess = new AtomicReference<>(ResultType.ERROR);

            Promotion promotion =
                    promotionRepository.findByProductId(pr.getProductId())
                            .filter(p -> p.getPromotionStatus() != PromotionStatus.ENDED)
                            .orElseGet(() -> {
                               Promotion promotion1 = new Promotion(
                                       pr.getAdminId(),
                                       pr.getProductId(),
                                       pr.getDiscountRate(),
                                       pr.getStartTime(),
                                       pr.getEndTime(),
                                       pr.getTotalQuantity()
                               );
                               if(pr.getStartTime().isAfter(LocalDateTime.now())) {
                                promotion1.changeStatus(PromotionStatus.SCHEDULER);
                               }
                               createdSuccess.set(ResultType.SUCCESS);
                               return promotionRepository.save(promotion1);

                            });
        }

        @Transactional
    public void promotionUpdateStatusById(Long id) {
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
                    promotion.changeStatus(PromotionStatus.ENDED);
                }
                break;
            default:
                break;
        }
    }

    @Transactional
    public void promotionUpdateById(Long id, PromotionRequest req) {

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + id));

        if (promotion.getPromotionStatus() != PromotionStatus.SCHEDULER) {
            throw new IllegalStateException( "현재 상태(" + promotion.getPromotionStatus() + ")에서는 수정할 수 없습니다. ");
        }

        promotion.updatePromotion(
                req.getAdminId(),
                req.getProductId(),
                req.getDiscountRate(),
                req.getStartTime(),
                req.getEndTime(),
                req.getTotalQuantity()
        );

        promotionRepository.save(promotion);
    }


    public void deletePromotion(Long Id) {
        promotionRepository.deleteById(Id);
    }


    public List<PromotionResponse> findAll(){
        return promotionRepository.findAll().stream().map(p -> new PromotionResponse(
                        p.getId(),
                        p.getAdminId(),
                        p.getProductId(),
                        p.getDiscountRate(),
                        p.getTotalQuantity(),
                        p.getStartTime(),
                        p.getEndTime()

                ))
                .toList();

    };


    public List<PromotionResponse> getPromotionsByStatus(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(promotionStatus).stream().map(p -> new PromotionResponse(
                p.getId(),
                p.getAdminId(),
                p.getProductId(),
                p.getDiscountRate(),
                p.getTotalQuantity(),
                p.getStartTime(),
                p.getEndTime()
        ))
                .toList();

    }
    public List<RedisPromotionResponse> returnSchedule() {
        return promotionRepository.findPromotionIdAndTotalQuantityBYPromotionStatusisSCHEDULE();
    }
    public List<RedisPromotionResponse> returnActive() {
        return promotionRepository.findPromotionIdAndTotalQuantityBYPromotionStatusisACTIVE();
    }

}


