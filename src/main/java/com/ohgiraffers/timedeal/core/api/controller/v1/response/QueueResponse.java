package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.enums.QueueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "대기열 상태 응답")
public record   QueueResponse(
        @Schema(description = "대기열 순번 (0부터 시작)", example = "100", minimum = "0", requiredMode = REQUIRED)
        @PositiveOrZero
        Long position,

        @Schema(description = "대기열 예상 대기 시간 (초)", example = "60", requiredMode = REQUIRED)
        @PositiveOrZero
        Long waitTime,

        @Schema(description = "대기열 상태", example = "WAITING", allowableValues = {"WAITING", "PROCEED", "EXPIRED"}, requiredMode = REQUIRED)
        @NotNull
        QueueStatus status
) {

}
