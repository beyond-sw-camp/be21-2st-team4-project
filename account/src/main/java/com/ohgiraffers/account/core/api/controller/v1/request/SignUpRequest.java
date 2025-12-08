package com.ohgiraffers.account.core.api.controller.v1.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "회원가입 생성 요청")
public record SignUpRequest(
        @NotBlank
        @Schema(description = "email 입력",example = "admin5",requiredMode = REQUIRED)
        String email,

        @Pattern(
                regexp = "^(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).+$",
                message = "비밀번호에는 특수문자가 1개 이상 포함되어야 합니다."
        )
        @NotBlank
        @Schema(description = "password 입력",example = "Pass1234!",requiredMode = REQUIRED)
        String password,

        @NotBlank
        @Schema(description = "이름 입력", example = "홍길동",requiredMode = REQUIRED)
        String name
){

}
