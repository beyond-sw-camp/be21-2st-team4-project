package com.ohgiraffers.account.core.api.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyPageResponse(
        @Schema(description = "이름")
        String name,

        @Schema(description = "가지고 있는 금액")
        int money,

        @Schema(description = "절약한 총 금액")
        int total_saved
){

}