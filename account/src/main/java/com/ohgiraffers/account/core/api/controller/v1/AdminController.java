package com.ohgiraffers.account.core.api.controller.v1;

import com.ohgiraffers.account.core.api.controller.v1.request.AdminRequest;
import com.ohgiraffers.account.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.account.core.domain.AdminService;
import com.ohgiraffers.common.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 1. 관리자 등록
    @PostMapping("/api/v1/admins")
    public ApiResult<AdminResponse> createAdmin(@RequestBody AdminRequest request) {
        return ApiResult.success(adminService.createAdmin(request));
    }

}