package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "로그인 생성 요청")
public record LoginRequest(
        @NotBlank
        @Schema(description = "email값 ", example = "admin3",requiredMode = REQUIRED)
        String email,

        @NotBlank
        @Schema(description = "비밀번호 값", example = "Pass1234!",requiredMode = REQUIRED)
        String password
){

}
