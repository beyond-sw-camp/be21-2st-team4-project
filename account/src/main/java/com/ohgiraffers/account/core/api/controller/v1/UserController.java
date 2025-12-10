package com.ohgiraffers.account.core.api.controller.v1;

import com.ohgiraffers.account.core.api.controller.v1.request.LoginRequest;
import com.ohgiraffers.account.core.api.controller.v1.request.SignUpRequest;
import com.ohgiraffers.account.core.api.controller.v1.request.VerifyTokenRequest;
import com.ohgiraffers.account.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.UserResponse;
import com.ohgiraffers.account.core.domain.UserService;
import com.ohgiraffers.common.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/users/signIn")
    public ApiResult<SignInResponse> signIn(@RequestBody LoginRequest request) {
        return ApiResult.success(userService.signIn(request.email(), request.password()));
    }

//    @Operation(summary = "로그아웃", description = "로그인 id를 받아 로그아웃")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
//            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
//    })
//    @PostMapping("/api/v1/users/signOut")
//    public ApiResult<?> signOut(
//            @Parameter(description = "유저 ID", example = "7")
//            @RequestParam Long userId
//    ){
//        userService.signOut(userId);
//        return ApiResult.success();
//    }

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원을 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일")
    })
    @PostMapping("/users/signUp")
    public ApiResult<?> signUp(@RequestBody @Valid SignUpRequest request){
        userService.signUp(request.email(), request.password(), request.name());
        return ApiResult.success();
    }

    @Operation(
            summary = "토큰값 인증",
            description = "아이디와 토큰값으로 인증"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (토큰 형식 오류 등)"),
            @ApiResponse(responseCode = "401", description = "토큰 불일치 또는 만료된 토큰"),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    })
    @PostMapping("/users/verify")
    public ApiResult<Boolean> verifyToken(@RequestBody VerifyTokenRequest verifyTokenRequest){
        return ApiResult.success(userService.verifyToken(verifyTokenRequest.userId(), verifyTokenRequest.token()));
    }


    @Operation(summary = "마이페이지 조회", description = "유저의 마이페이지 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    @GetMapping("/users/me")
    public ApiResult<MyPageResponse> getMe(@AuthenticationPrincipal String userId) {
        System.out.println("userId = " + userId);
        return ApiResult.success(userService.getMe(Long.parseLong(userId)));
    }

    @GetMapping("/users/me/orders")
    public ApiResult<OrderDetailResponse> getMeOrders(@AuthenticationPrincipal String userId) {
        return ApiResult.success(userService.getMeOrders(Long.parseLong(userId)));
    }

    @GetMapping("/users/{id}")
    ApiResult<UserResponse> getUser(Long id) {
        return ApiResult.success(userService.getUser(id));
    }

    @GetMapping("/users/decreaseMoney")
    ApiResult<?> decreaseMoney(@RequestParam Long id, @RequestParam Integer price) {
        userService.decreaseMoney(id, price);
        return ApiResult.success();
    }
}

