package com.ohgiraffers.queue.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ohgiraffers.queue.core.enums.PromotionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

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
