package com.ohgiraffers.product.core.api.controller.v1.response;

import com.ohgiraffers.product.core.domain.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}