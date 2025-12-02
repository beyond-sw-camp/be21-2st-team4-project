package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Product;
import com.ohgiraffers.timedeal.core.domain.Category; // Category import 필요

// record class로 변경
public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer price,
        String imageUrl,
        String categoryName, // 💡 FIX: categoryName으로 변경
        Long adminId
)
{
    public static ProductResponse from(Product product) {
        // 💡 FIX: Category 객체에서 name을 가져오도록 수정
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                categoryName,
                product.getAdminId()
        );
    }
}