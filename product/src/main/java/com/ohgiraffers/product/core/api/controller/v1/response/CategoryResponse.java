package com.ohgiraffers.product.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Category;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CategoryResponse {
    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryResponse from(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.id = category.getId();
        response.name = category.getName();
        response.status = category.getStatus().name();
        response.createdAt = category.getCreatedAt();
        response.updatedAt = category.getUpdatedAt();
        return response;
    }
}