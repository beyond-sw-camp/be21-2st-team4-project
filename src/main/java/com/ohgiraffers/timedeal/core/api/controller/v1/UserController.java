package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.LoginRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.request.SignUpRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.domain.UserService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users/signIn")
    public ApiResult<Boolean> signIn(@RequestBody LoginRequest request) {
        Boolean result = userService.signIn(request.getEmail(), request.getPassword());
        return ApiResult.success(result);

    }

    @PostMapping("/api/v1/users/signUp")
    public ApiResult<Boolean> signUp(@RequestBody SignUpRequest request){
        Boolean result = userService.signUp(request.getEmail(), request.getPassword(),request.getName());
        return ApiResult.success(result);
    }

    @GetMapping("/api/v1/users/my-page")
    public ApiResult<MyPageResponse> getMyPage(@RequestBody MyPageRequest request) {
        MyPageResponse myPage = userService.getMyPage(request);
        return ApiResult.success(myPage);
    }


}
