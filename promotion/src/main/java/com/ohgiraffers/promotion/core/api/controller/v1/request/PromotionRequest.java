package com.ohgiraffers.promotion.core.api.controller.v1.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequest {
    private Long adminId;
    private Long productId;
    private Double discountRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalQuantity;
}
