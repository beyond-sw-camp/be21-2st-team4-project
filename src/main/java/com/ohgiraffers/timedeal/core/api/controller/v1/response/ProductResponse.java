package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Product;

// record class로 변경
public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer price,
        String imageUrl,
        String category,
        Long adminId
)
{
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getAdminId()
        );
    }
}