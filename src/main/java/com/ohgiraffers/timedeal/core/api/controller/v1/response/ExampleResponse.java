package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "예제 응답")
public record ExampleResponse(
        @Schema(description = "예제 리스트", example = "hello-world...", requiredMode = REQUIRED)
        List<String> examples
) {

}
