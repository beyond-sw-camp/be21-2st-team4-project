package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.support.response.ResultType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PromotionResponse (
        Long promotionId,
        Long productId,
        Double discountRate,
        Integer totalQuantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ResultType getSuccess){

    public static  PromotionResponse of(Promotion promotion){

    }
}
