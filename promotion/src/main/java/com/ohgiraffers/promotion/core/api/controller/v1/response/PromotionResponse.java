package com.ohgiraffers.promotion.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "프로모션 저장 정보")
public record PromotionResponse (
        Long id,
        String company,
        String productName,
        Integer originalPrice,
        Integer salePrice,
        Double discountRate,
        Integer totalQuantity,
        Integer soldQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        String productImageUrl
        ){

}
