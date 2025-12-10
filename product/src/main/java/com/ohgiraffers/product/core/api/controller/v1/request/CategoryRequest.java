package com.ohgiraffers.product.core.api.controller.v1.request;

public record CategoryRequest(
        String name,
        String status       // "ACTIVE" 또는 "DELETED"
) {

}