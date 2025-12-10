package com.ohgiraffers.promotion.core.domain;

import com.ohgiraffers.common.constants.TimedealKeys;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.promotion.core.api.command.CommandClient;
import com.ohgiraffers.promotion.core.api.command.ProductClient;
import com.ohgiraffers.promotion.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.response.*;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import com.ohgiraffers.promotion.storage.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final CommandClient commandClient;

    @Transactional
    public void updateSoldQuantity(OrderRequest orderRequest) {
        Promotion promotion = promotionRepository.findPromotionById(orderRequest.getId());
        int soldQuantity = orderRequest.getSoldQuantity() + promotionRepository.findSoldQuantityById(orderRequest.getId());

        promotionRepository.updatePromotionSoldQuantity(orderRequest.getId(), soldQuantity);
        String key = TimedealKeys.setPromotion(promotion.getId());
        stringRedisTemplate.opsForValue().decrement(key, orderRequest.getSoldQuantity());
    }

    ;

    //프로모션 생성(이미 진행하고있는 프로모션이 있나 비교)

    @Transactional
    public void promotionSave(Long userId, PromotionRequest pr) {
        AtomicReference<ResultType> createdSuccess = new AtomicReference<>(ResultType.ERROR);


        ProductResponse product = commandClient.getProduct(pr.getProductId());
        if (product == null) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        Promotion promotion =
                promotionRepository.findByProductId(pr.getProductId())
                        .filter(p -> p.getPromotionStatus() != PromotionStatus.ENDED)
                        .orElseGet(() -> {
                            Promotion promotion1 = new Promotion(
                                    userId,
                                    pr.getProductId(),
                                    pr.getDiscountRate(),
                                    pr.getStartTime(),
                                    pr.getEndTime(),
                                    pr.getTotalQuantity()
                            );
                            if (pr.getStartTime().isAfter(LocalDateTime.now())) {
                                promotion1.changeStatus(PromotionStatus.SCHEDULER);
                            }
                            createdSuccess.set(ResultType.SUCCESS);
                            return promotionRepository.save(promotion1);
                        });
        promotion.setSalePrice((int) (pr.getDiscountRate() * product.price()));


    }

    @Transactional
    public void promotionUpdateById(
            Long userId, Long id, PromotionRequest req) {

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + id));

        if (promotion.getPromotionStatus() != PromotionStatus.SCHEDULER) {
            throw new IllegalStateException("현재 상태(" + promotion.getPromotionStatus() + ")에서는 수정할 수 없습니다. ");
        }

        promotion.updatePromotion(
                userId,
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
        if (promotionStatus.equals(PromotionStatus.SCHEDULER) && promotion.getStartTime().isBefore(LocalDateTime.now())) {
            promotion.changeStatus(PromotionStatus.ACTIVE);
        } else if (promotionStatus.equals(PromotionStatus.ACTIVE) && promotion.getEndTime().isBefore(LocalDateTime.now())) {
            promotion.changeStatus(PromotionStatus.ENDED);
        }
    }


    public void deletePromotion(Long Id) {
        promotionRepository.deleteById(Id);
    }


    public List<PromotionResponse> findAll() {
        List<PromotionResponse> result = new ArrayList<>();
        List<Promotion> promotions = promotionRepository.findAll();
        for (Promotion promotion : promotions) {
            ProductResponse product = commandClient.getProduct(promotion.getProductId());
            String company = commandClient.getCompany(promotion.getAdminId());
            result.add(new PromotionResponse(
                    promotion.getId(),
                    company,
                    product.name(),
                    product.price(),
                    promotion.getSalePrice(),
                    promotion.getDiscountRate(),
                    promotion.getTotalQuantity(),
                    promotion.getStartTime(),
                    promotion.getEndTime(),
                    product.imageUrl()
            ));
        }
        return result;
    }

    ;


    public PromotionListResponse getPromotionsByStatus(String promotionStatus) {

        PromotionStatus status = PromotionStatus.valueOf(promotionStatus);
        if (status == null) {
            return null;
        }
        List<Promotion> promotions = promotionRepository.findAllByPromotionStatus(status);
        List<PromotionResponse> result = new ArrayList<>();
        for (Promotion promotion : promotions) {
            String company = commandClient.getCompany(promotion.getAdminId());
            ProductResponse product = commandClient.getProduct(promotion.getProductId());
            result.add(new PromotionResponse(
                    promotion.getId(),
                    company,
                    product.name(),
                    product.price(),
                    promotion.getSalePrice(),
                    promotion.getDiscountRate(),
                    promotion.getTotalQuantity(),
                    promotion.getStartTime(),
                    promotion.getEndTime(),
                    product.imageUrl()
            ));
        }
        PromotionListResponse promotionListResponse = new PromotionListResponse(result);
        return promotionListResponse;

    }

    public List<RedisPromotionResponse> returnSchedule(PromotionStatus promotionStatus) {
        List<Promotion> promotion = promotionRepository.findAllByPromotionStatus(promotionStatus);
        return promotion.stream().map(p -> new RedisPromotionResponse(
                        p.getId(),
                        p.getTotalQuantity()
                ))
                .toList();
    }

/*    public List<RedisPromotionResponse> returnActive(PromotionStatus promotionStatus) {
        return promotionRepository.findAllByPromotionStatus(PromotionStatus.ACTIVE);
    }*/

    @Transactional
    public void updatePromotionStatus(Long id, PromotionStatus promotionStatus) {
        promotionRepository.updatePromotionStatus(id, promotionStatus);
    }

    public PromotionResponse findPromotionById(Long id) {
        Promotion promotion = promotionRepository.findPromotionById(id);
        ProductResponse product = commandClient.getProduct(promotion.getProductId());
        String company = commandClient.getCompany(promotion.getAdminId());

        return new PromotionResponse(
                promotion.getId(),
                company,
                product.name(),
                product.price(),
                promotion.getSalePrice(),
                promotion.getDiscountRate(),
                promotion.getTotalQuantity(),
                promotion.getStartTime(),
                promotion.getEndTime(),
                product.imageUrl()
        );
    }

    public List<Promotion> updateStatus() {
        return promotionRepository.findAll();
    }

    public OrderResponse findOrderResponseById(long id) {
        Promotion promotion = promotionRepository.findPromotionById(id);
        return new OrderResponse(promotion.getId(), promotion.getSalePrice(), promotion.getTotalQuantity(), promotion.getPromotionStatus());
    }


}

