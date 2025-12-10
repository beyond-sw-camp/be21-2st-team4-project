package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.UserResponse;
import com.ohgiraffers.order.core.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "timedeal-account-service", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    ApiResult<UserResponse> getUser(@PathVariable Long id);

    @GetMapping("/users/decreaseMoney")
    ApiResult<Object> decreaseMoney(
            @RequestParam Long id,
            @RequestParam Integer price);
}