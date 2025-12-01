package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Product;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 목록 응답 DTO (Record Class)
 */
public record ProductListResponse(
        List<ProductResponse> products
) {
    /**
     * Product 엔티티 리스트를 ProductListResponse DTO로 변환합니다.
     * * @param products Product 엔티티 리스트
     * @return ProductListResponse
     */
    public static ProductListResponse from(List<Product> products) {
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return new ProductListResponse(productResponses);
    }
}