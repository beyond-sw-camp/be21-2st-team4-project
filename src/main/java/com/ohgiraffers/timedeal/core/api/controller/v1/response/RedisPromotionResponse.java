package com.ohgiraffers.timedeal.core.api.controller.v1.response;

public record RedisPromotionResponse(
        Long timedealId,
        int totalQuantity
) {
}
