package com.ohgiraffers.account.core.api.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminLoginRequest(
        @NotBlank
        @Schema(description = "email값 ", example = "admin1",requiredMode = REQUIRED)
        String email,

        @NotBlank
        @Schema(description = "비밀번호 값", example = "Pass1234!",requiredMode = REQUIRED)
        String password
) {
}
