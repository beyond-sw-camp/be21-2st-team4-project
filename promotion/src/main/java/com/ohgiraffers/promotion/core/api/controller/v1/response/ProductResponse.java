package com.ohgiraffers.promotion.core.api.controller.v1.response;

public record ProductResponse(
        Long id,
        String companyName,
        String categoryName,
        String name,
        String description,
        Integer price,
        String imageUrl
) {
}