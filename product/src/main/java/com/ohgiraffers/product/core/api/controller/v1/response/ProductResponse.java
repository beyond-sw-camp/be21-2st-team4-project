package com.ohgiraffers.product.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer price,
        String imageUrl,
        Long categoryId,
        Long adminId
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getAdmin() != null ? product.getAdmin().getId() : null
        );
    }
}