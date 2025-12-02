package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.LoginRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.request.SignUpRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.timedeal.core.domain.UserService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "User API", description = "회원가입, 로그인, 마이페이지 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "이메일/비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "해당 이메일의 유저가 존재하지 않음")
    })
    @PostMapping("/api/v1/users/signIn")
    public ApiResult<SignInResponse> signIn(@RequestBody LoginRequest request) {
        return ApiResult.success(userService.signIn(request.email(), request.password()));
    }

    @Operation(summary = "로그아웃", description = "로그인 id를 받아 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    })
    @PostMapping("/api/v1/users/signOut")
    public ApiResult<?> signOut(
            @Parameter(description = "유저 ID", example = "7")
            @RequestParam Long userId
    ){
        userService.signOut(userId);
        return ApiResult.success();
    }

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원을 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일")
    })
    @PostMapping("/api/v1/users/signUp")
    public ApiResult<?> signUp(@RequestBody @Valid SignUpRequest request){
        userService.signUp(request.email(), request.password(), request.name());
        return ApiResult.success();
    }

    @Operation(summary = "마이페이지 조회", description = "유저의 마이페이지 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    @GetMapping("/api/v1/users/me")
    public ApiResult<MyPageResponse> getMe(
            @Parameter(description = "유저 ID", example = "7") @RequestParam Long userId
    ) {
        return ApiResult.success(userService.getMe(userId));
    }

    @Operation(summary = "주문내역 조회", description = "유저의 마이페이지 주문내역 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저 또는 주문내역을 찾을 수 없음")
    })
    @GetMapping("/api/v1/users/me/orders")
    public ApiResult<OrderDetailResponse> getMeOrders(
            @Parameter(description = "유저 ID", example = "7") @RequestParam Long userId
    ) {
        return ApiResult.success(userService.getMeOrders(userId));
    }
}

