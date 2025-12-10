package com.ohgiraffers.order.core.api.controller.v1.response;

import com.ohgiraffers.order.core.api.command.common.PromotionStatus;

public record OrderResponse(
        Long Id,
        Integer salePrice,
        Integer totalQuantity,
        PromotionStatus promotionStatus
) {

}
