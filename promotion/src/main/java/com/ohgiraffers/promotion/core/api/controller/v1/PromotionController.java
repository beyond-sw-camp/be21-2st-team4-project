package com.ohgiraffers.promotion.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.promotion.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.promotion.core.api.controller.v1.response.OrderResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionListResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.domain.PromotionService;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PromotionController {
    private PromotionService promotionService;


    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    // 새로운 프로모션 추가
    @PostMapping
    public ApiResult<?> save(@RequestBody PromotionRequest promotionRequest) {
        promotionService.promotionSave(promotionRequest);
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    public ApiResult<?> update(
            @PathVariable long id,
            @RequestBody PromotionRequest promotionRequest) {
        promotionService.promotionUpdateById(id, promotionRequest);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<?> deleteById(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ApiResult.success();
    }

    @GetMapping
    public ApiResult<List<PromotionResponse>> getAllPromotion() {
        return ApiResult.success(promotionService.findAll());
    }

    @GetMapping("/status/{promotionStatus}")
    public ApiResult<PromotionListResponse> getPromotionsStatusAll(@PathVariable String promotionStatus) {
        return ApiResult.success(promotionService.getPromotionsByStatus(promotionStatus));
    }

    @GetMapping("/{id}")
    public ApiResult<PromotionResponse> findPromotionById(
            @PathVariable long id
    ) {
        return ApiResult.success(promotionService.findPromotionById(id));
    }

    @PostMapping("/order")
    public ApiResult<?> checkTotalQuantity(@RequestBody OrderRequest orderRequest) {
        promotionService.updateSoldQuantity(orderRequest);
        return ApiResult.success();
    }
}
