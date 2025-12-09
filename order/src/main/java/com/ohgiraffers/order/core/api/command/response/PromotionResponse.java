package com.ohgiraffers.order.core.api.command.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ohgiraffers.order.core.api.command.common.PromotionStatus;

import java.time.LocalDateTime;

public record PromotionResponse(
        Long id,
        Long productId,
        Integer salePrice,
        Integer totalQuantity,
        Integer soldQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        PromotionStatus promotionStatus
) {
}
