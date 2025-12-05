package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.response.ResultType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "프로모션 저장 정보")
public record PromotionResponse (
        @Schema(description = "")
        Long id,
        Long adminId,
        Long productId,
        Integer salePrice,
        Double discountRate,
        Integer totalQuantity,
        Integer soldQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        String productName,
        String productImage,
        Integer originalPrice,
        PromotionStatus promotionStatus
        ){

}
