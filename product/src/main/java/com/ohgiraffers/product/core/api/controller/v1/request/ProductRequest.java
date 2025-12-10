package com.ohgiraffers.product.core.api.controller.v1.request;

public record ProductRequest(
        Long categoryId,
        String name,
        String description,
        Integer price,
        String imageUrl
) {

}