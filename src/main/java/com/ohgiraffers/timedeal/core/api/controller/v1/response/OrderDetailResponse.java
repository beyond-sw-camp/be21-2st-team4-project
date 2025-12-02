package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record OrderDetailResponse(
        @Schema(description = "주문내역 리스트",requiredMode = REQUIRED)
        List<MyPageOrderResponse> myPageOrderResponseList
){

}