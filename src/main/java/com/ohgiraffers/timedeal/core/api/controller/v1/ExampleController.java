package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.ExampleRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ExampleResponse;
import com.ohgiraffers.timedeal.core.domain.ExampleService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 *  Swagger
 *  접속 : http://localhost:8080/swagger-ui/index.html
 */

@Tag(name = "Example", description = "예제 관련 API")
@RestController
@RequiredArgsConstructor
// @RequestMapping은 사용하지 않음
public class ExampleController {
    private final ExampleService exampleService;

    @GetMapping("/api/v1/examples")
    @Operation(summary = "예제 조회", description = "모든 예제를 조회합니다.") // summary = 간단하게, description = 상세하게
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),     // 성공하는 경우
            @ApiResponse(responseCode = "400", description = "에러1"),    // 서비스, 저장소에서 발생할 수 있는 에러 적기
            @ApiResponse(responseCode = "404", description = "에러2")     // .e.g) 잘못된 요청, 유저를 찾을 수 없음
    })
    public ApiResult<ExampleResponse> getExamples() {
        return ApiResult.success(exampleService.getExamples());
    }

    @PostMapping("api/v1/examples/{example}")
    @Operation(summary = "예제 생성1", description = "새로운 예제를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ApiResult<?> postExample(
            @Parameter(description = "예제", example = "hello-world", required = true)    // @PathVariable, @RequestParam, @RequestBody등에 @Parameter를 추가하여 의미를 명확히 전달
            @PathVariable String example
    ) {
        exampleService.postExample(example);
        return ApiResult.success();
    }

    @PostMapping("api/v1/examples/")
    @Operation(summary = "예제 생성2", description = "새로운 예제를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ApiResult<?> postExample(
            @Parameter(description = "예제", example = "hello-world", required = true)    // example은 실제 사용가능한 값으로 작성
            @RequestBody ExampleRequest request
    ) {
        exampleService.postExample(request.example());
        return ApiResult.success();
    }
}
