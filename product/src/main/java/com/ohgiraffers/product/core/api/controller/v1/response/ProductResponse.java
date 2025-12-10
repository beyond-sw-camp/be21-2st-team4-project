package com.ohgiraffers.product.core.api.controller.v1.response;

import com.ohgiraffers.product.core.domain.Product;

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