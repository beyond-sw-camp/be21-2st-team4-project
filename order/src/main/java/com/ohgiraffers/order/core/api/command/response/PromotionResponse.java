package com.ohgiraffers.order.core.api.command.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ohgiraffers.order.core.api.command.common.PromotionStatus;

import java.time.LocalDateTime;

public record PromotionResponse (
        Long id,
        String company,
        String productName,
        Integer originalPrice,
        Integer salePrice,
        Integer discountRate,
        Integer totalQuantity,
        Integer soldQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        String productImageUrl,
        PromotionStatus promotionStatus
        ){

}
