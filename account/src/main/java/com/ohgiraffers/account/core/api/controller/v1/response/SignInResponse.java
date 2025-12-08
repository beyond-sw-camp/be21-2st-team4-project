package com.ohgiraffers.account.core.api.controller.v1.response;

public record SignInResponse(
        Long userId,
        String token
) {
}
