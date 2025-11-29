package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.QueueEnterRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.QueueResponse;
import com.ohgiraffers.timedeal.core.domain.QueueService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대기열", description = "대기열 API")
@RestController
@Validated
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/api/v1/queue")
    @Operation(summary = "대기열 참가", description = "사용자가 특정 타임딜 대기열에 참가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열에 참여함"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "타임딜을 찾을 수 없음")
    })
    public ApiResult<QueueResponse> enterQueue(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "대기열 참가 요청 정보",
                    required = true
            )
            @RequestBody @Valid QueueEnterRequest request
    ) {
        return ApiResult.success(queueService.enterQueue(request.timedealId(), request.userId()));
    }

    @GetMapping("/api/v1/queue")
    @Operation(summary = "대기열 상태", description = "사용자가 특정 타임딜 대기열 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열 상태를 확인"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음")
    })
    public ApiResult<QueueResponse> getQueue(
            @Parameter(description = "타임딜 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "timedealId") Long timedealId,

            @Parameter(description = "유저 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "userId") Long userId
    ) {
        return ApiResult.success(queueService.getQueue(timedealId, userId));
    }
}
