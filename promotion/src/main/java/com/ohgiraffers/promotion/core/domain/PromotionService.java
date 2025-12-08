package com.ohgiraffers.promotion.core.domain;

import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.promotion.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.response.OrderResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import com.ohgiraffers.promotion.storage.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    public void updateSoldQuantity(OrderRequest orderRequest) {
        Long promotionSoldQuantity = promotionRepository.findSoldQuantityById(orderRequest.getId());
        promotionRepository.updatePromotionSoldQuantity(orderRequest.getId(), orderRequest.getSoldQuantity() + promotionSoldQuantity);
    };

    //프로모션 생성(이미 진행하고있는 프로모션이 있나 비교)
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
    @Transactional
    public void changePromotionStatus(Long id) {
        PromotionStatus promotionStatus = promotionRepository.findPromotionStatusById(id);
        Promotion promotion = promotionRepository.findPromotionById(id);
        if(promotionStatus.equals(PromotionStatus.SCHEDULER) && promotion.getStartTime().isBefore(LocalDateTime.now())) {
            promotion.changeStatus(PromotionStatus.ACTIVE);
        }
        else if(promotionStatus.equals(PromotionStatus.ACTIVE) && promotion.getEndTime().isBefore(LocalDateTime.now())) {
            promotion.changeStatus(PromotionStatus.ENDED);
        }
    }


    public void deletePromotion(Long Id) {
        promotionRepository.deleteById(Id);
    }


    public List<PromotionResponse> findAll(){
        return promotionRepository.findAllPromotions().stream().map(p -> new PromotionResponse(
                        p.id(),
                        p.adminId(),
                        p.productId(),
                        p.salePrice(),
                        p.discountRate(),
                        p.totalQuantity(),
                        p.startTime(),
                        p.endTime()
                ))
                .toList();

    };


    public List<PromotionResponse> getPromotionsByStatus(PromotionStatus promotionStatus) {
        return promotionRepository.findPromotionByPromotionStatus(promotionStatus).stream().map(p -> new PromotionResponse(
                p.id(),
                p.adminId(),
                p.productId(),
                p.salePrice(),
                p.discountRate(),
                p.totalQuantity(),
                p.startTime(),
                p.endTime()
        ))
                .toList();

    }
    public List<RedisPromotionResponse> returnSchedule(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(PromotionStatus.SCHEDULER);
    }
    public List<RedisPromotionResponse> returnActive(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(PromotionStatus.ACTIVE);
    }

    @Transactional
    public void updatePromotionStatus(Long id, PromotionStatus promotionStatus) {
        promotionRepository.updatePromotionStatus(id, promotionStatus);
    }

    public Promotion findPromotionById(long id) {
        return promotionRepository.findPromotionById(id);
    }

    public List<Promotion> updateStatus(){
        return promotionRepository.findAll();
    }

    public OrderResponse findOrderResponseById(long id) {
        return promotionRepository.findOrderResponseById(id);
    }
}

