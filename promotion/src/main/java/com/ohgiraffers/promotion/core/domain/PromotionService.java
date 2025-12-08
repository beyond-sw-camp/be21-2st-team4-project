package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.timedeal.storage.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.ohgiraffers.common.support.response.ApiResult.success;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final ProductRepository  productRepository;

    //프로모션 생성(이미 진행하고있는 프로모션이 있나 비교)
    public void promotionSave(PromotionRequest pr) {
            AtomicReference<ResultType> createdSuccess = new AtomicReference<>(ResultType.ERROR);

        Product product = productRepository.findById(pr.getProductId()).orElseThrow();

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
                               promotion1.setSalePrice((int) (product.getPrice() * promotion1.getDiscountRate()));
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
                        p.endTime(),
                        p.productName(),
                        p.productImage(),
                        p.originalPrice()


                ))
                .toList();

    };


    public List<PromotionResponse> getPromotionsByStatus(PromotionStatus promotionStatus) {
        return promotionRepository.findByPromotionStatus(promotionStatus).stream().map(p -> new PromotionResponse(
                p.id(),
                p.adminId(),
                p.productId(),
                p.salePrice(),
                p.discountRate(),
                p.totalQuantity(),
                p.startTime(),
                p.endTime(),
                p.productName(),
                p.productImage(),
                p.originalPrice()
        ))
                .toList();

    }
    public List<RedisPromotionResponse> returnSchedule(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(PromotionStatus.SCHEDULER);
    }
    public List<RedisPromotionResponse> returnActive(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(PromotionStatus.ACTIVE);
    }

    public Promotion findPromotionById(long id) {
        return promotionRepository.findPromotionById(id);
    }
    public List<Promotion> updateStatus(){
        return promotionRepository.findAll();
    }
}

