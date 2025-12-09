package com.ohgiraffers.queue.core.api.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "대기열 참가 요청")
public record QueueEnterRequest(
        @Schema(description = "타임딜 아이디", example = "1")
        @NotNull(message = "타임딜 ID는 필수입니다")
        @Positive(message = "타임딜 ID는 양수여야 합니다")
        Long timedealId
) {

}
