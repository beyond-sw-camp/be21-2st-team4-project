package com.ohgiraffers.order.core.api.command.response;

public record UserResponse(
        Long id,
        String email,
        Integer money
) {
}
