package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Product;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> products
) {
    public static ProductListResponse from(List<Product> products) {
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::from)
                .toList();
        return new ProductListResponse(responses);
    }
}