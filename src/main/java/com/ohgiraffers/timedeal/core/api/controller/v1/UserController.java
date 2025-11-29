package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.LoginRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.request.SignUpRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageOrderResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.domain.UserService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users/signIn")
    public ApiResult<?> signIn(@RequestBody LoginRequest request) {
        userService.signIn(request.getEmail(), request.getPassword());
        return ApiResult.success();

    }

    @PostMapping("/api/v1/users/signUp")
    public ApiResult<?> signUp(@RequestBody SignUpRequest request){
        userService.signUp(request.getEmail(), request.getPassword(),request.getName());
        return ApiResult.success();
    }

    //1. 마이페이지 + 주문 내역
//    @GetMapping("/api/v1/users/me")
//    public ApiResult<MyPageResponse> getMyPage(@RequestParam Long userId) {
//        return ApiResult.success(userService.getMyPage(userId));
//    }

    //2-1. 마이페이지
    @GetMapping("/api/v1/users/me")
    public ApiResult<MyPageResponse> getMe(@RequestParam Long userId) {
        return ApiResult.success(userService.getMe(userId));
    }

    //2-2. 주문내역
    @GetMapping("/api/v1/users/me/orders")
    public ApiResult<OrderDetailResponse> getMeOrders(@RequestParam Long userId) {
        return ApiResult.success(userService.getMeOrders(userId));
    }

}
