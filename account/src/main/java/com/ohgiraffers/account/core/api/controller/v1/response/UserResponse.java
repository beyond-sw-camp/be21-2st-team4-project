package com.ohgiraffers.account.core.api.controller.v1.response;

public record UserResponse(
        Long id,
        String email,
        Integer money
) {
}
