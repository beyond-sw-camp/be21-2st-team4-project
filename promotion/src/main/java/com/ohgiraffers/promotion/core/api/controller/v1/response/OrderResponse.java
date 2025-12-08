package com.ohgiraffers.promotion.core.api.controller.v1.response;

import com.ohgiraffers.promotion.core.enums.PromotionStatus;

public record OrderResponse(
        Long promotionId,
        Long salePrice,
        Long totalQuantity,
        PromotionStatus promotionStatus
) {

}
