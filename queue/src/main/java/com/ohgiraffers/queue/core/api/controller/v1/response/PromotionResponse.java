package com.ohgiraffers.queue.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PromotionResponse(
        Long id,
        Long adminId,
        Long productId,
        Integer salePrice,
        Double discountRate,
        Integer totalQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        String productName,
        String productImage,
        Integer originalPrice
) {

}
