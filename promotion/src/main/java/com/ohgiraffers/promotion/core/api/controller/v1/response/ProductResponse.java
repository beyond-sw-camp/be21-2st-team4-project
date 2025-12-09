package com.ohgiraffers.promotion.core.api.controller.v1.response;

public record ProductResponse(
        String ProductName,
        Long OriginalPrice,
        String imageUrl
) {
}
