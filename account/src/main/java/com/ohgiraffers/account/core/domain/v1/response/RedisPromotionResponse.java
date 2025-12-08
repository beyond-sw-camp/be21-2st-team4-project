package com.ohgiraffers.account.core.domain.v1.response;

public record RedisPromotionResponse(
        Long timedealId,
        int totalQuantity
) {
}
