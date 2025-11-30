package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.LoginRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.request.SignUpRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.domain.UserService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "회원가입, 로그인, 마이페이지 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @PostMapping("/signIn")
    public ApiResult<?> signIn(@RequestBody LoginRequest request) {
        userService.signIn(request.getEmail(), request.getPassword());
        return ApiResult.success();
    }

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원을 생성")
    @PostMapping("/signUp")
    public ApiResult<?> signUp(@RequestBody SignUpRequest request){
        userService.signUp(request.getEmail(), request.getPassword(), request.getName());
        return ApiResult.success();
    }

    @Operation(summary = "마이페이지 조회", description = "유저의 마이페이지 정보를 조회")
    @GetMapping("/me")
    public ApiResult<MyPageResponse> getMe(@RequestParam Long userId) {
        return ApiResult.success(userService.getMe(userId));
    }

    @Operation(summary = "주문내역 조회", description = "유저의 마이페이지 주문내역 조회")
    @GetMapping("/me/orders")
    public ApiResult<OrderDetailResponse> getMeOrders(@RequestParam Long userId) {
        return ApiResult.success(userService.getMeOrders(userId));
    }
}

