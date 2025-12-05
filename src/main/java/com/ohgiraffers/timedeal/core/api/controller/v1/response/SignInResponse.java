package com.ohgiraffers.timedeal.core.api.controller.v1.response;

public record SignInResponse(
        Long userId,
        String token
) {
}
