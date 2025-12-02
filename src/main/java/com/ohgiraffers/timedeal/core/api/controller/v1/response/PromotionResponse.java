package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.support.response.ResultType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PromotionResponse (
        Long id,
        Long adminId,
        Long productId,
        Double discountRate,
        Integer totalQuantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime
        ){

}
