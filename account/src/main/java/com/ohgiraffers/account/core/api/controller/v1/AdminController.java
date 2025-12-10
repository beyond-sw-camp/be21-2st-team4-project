package com.ohgiraffers.account.core.api.controller.v1;

import com.ohgiraffers.account.core.api.controller.v1.request.AdminLoginRequest;
import com.ohgiraffers.account.core.api.controller.v1.request.AdminRequest;
import com.ohgiraffers.account.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.AdminSignInResponse;
import com.ohgiraffers.account.core.domain.AdminService;
import com.ohgiraffers.common.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 1. 관리자 등록
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원을 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일")
    })
    @PostMapping("/admin/signUp")
    public ApiResult<?> createAdmin(@RequestBody @Valid AdminRequest request) {
        adminService.createAdmin(request.email(),request.password(),request.company());
        return ApiResult.success();
    }

    // 2. 관리자 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "이메일/비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "해당 이메일의 유저가 존재하지 않음")
    })
    @PostMapping("/admin/signIn")
    public ApiResult<AdminSignInResponse> adminSignIn(@RequestBody AdminLoginRequest request) {
        return ApiResult.success(adminService.adminSignIn(request.email(), request.password()));
    }

    @GetMapping("/admin/{id}")
    public ApiResult<AdminResponse> getAdmin(@PathVariable Long id) {
        return ApiResult.success(adminService.getAdmin(id));
    }

}