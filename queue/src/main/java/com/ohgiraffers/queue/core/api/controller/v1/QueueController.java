package com.ohgiraffers.queue.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.queue.core.api.controller.v1.request.QueueEnterRequest;
import com.ohgiraffers.queue.core.api.controller.v1.response.QueueStatusResponse;
import com.ohgiraffers.queue.core.domain.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "대기열", description = "대기열 API")
@RestController
@Validated
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/api/v1/queues")
    @Operation(summary = "대기열 참가", description = "사용자가 특정 타임딜 대기열에 참가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열에 참여함"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "타임딜을 찾을 수 없음")
    })
    public ApiResult<QueueStatusResponse> enterQueue(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "대기열 참가 요청 정보",
                    required = true
            )
            @RequestBody @Valid QueueEnterRequest request
    ) {
        return ApiResult.success(queueService.enterQueue(request.timedealId(), request.userId()));
    }

    @DeleteMapping("/api/v1/queues")
    @Operation(summary = "대기열 나가기", description = "사용자가 특정 타임딜 대기열에서 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열에 참여함"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "타임딜을 찾을 수 없음")
    })
    public ApiResult<Boolean> leaveQueue(
            @Parameter(description = "타임딜 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "timedealId") Long timedealId,

            @Parameter(description = "유저 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "userId") Long userId
    ) {
        return ApiResult.success(queueService.leaveQueue(timedealId, userId));
    }

    @GetMapping("/api/v1/queues/verify")
    @Operation(summary = "대기열 검증", description = "사용자가 특정 타임딜 대기열을 통과했는지 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열 상태를 확인"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음"),
            @ApiResponse(responseCode = "410", description = "유효기간이 만료되었음")
    })
    public ApiResult<?> verifyQueue(
            @Parameter(description = "타임딜 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "timedealId") Long timedealId,

            @Parameter(description = "유저 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "userId") Long userId
    ) {
        queueService.verifyQueue(timedealId, userId);
        return ApiResult.success();
    }

    @GetMapping("/api/v1/queues")
    @Operation(summary = "대기열 상태", description = "사용자가 특정 타임딜 대기열 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대기열 상태를 확인"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 id)"),
            @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음")
    })
    public ApiResult<QueueStatusResponse> getQueueStatus(
            @Parameter(description = "타임딜 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "timedealId") Long timedealId,

            @Parameter(description = "유저 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "userId") Long userId
    ) {
        return ApiResult.success(queueService.getQueueStatus(timedealId, userId));
    }

    @GetMapping(value = "/api/v1/queues/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "대기열 SSE 생성 및 반환", description = "대기열 SSE 생성 및 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 SseEmitter 반환")
    })
    public SseEmitter getQueueSubscribe(
            @Parameter(description = "유저 아이디", example = "1", required = true)
            @Positive
            @RequestParam(value = "userId") Long userId
    ) {
        return queueService.getQueueSubscribe(userId);
    }
}
