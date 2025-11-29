package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예제 생성 요청")
public record ExampleRequest(
        @Schema(description = "예제", example = "hello-world")
        String example
) {

}
