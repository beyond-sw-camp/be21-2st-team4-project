package com.ohgiraffers.promotion.core.api.controller.v1.response;

import com.ohgiraffers.promotion.core.enums.PromotionStatus;

public record OrderResponse(
        Long Id,
        Integer salePrice,
        Integer totalQuantity,
        PromotionStatus promotionStatus
) {

}
