package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import com.ohgiraffers.timedeal.core.domain.Promotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PromotionRequest {
    private Long adminId;
    private Long productID;
    private Double discountRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalQuantity;
}
