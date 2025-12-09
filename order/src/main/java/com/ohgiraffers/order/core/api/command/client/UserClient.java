package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.UserResponse;
import com.ohgiraffers.order.core.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "timedeal-user-service", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    ApiResult<UserResponse> getUser(Long id);

    @GetMapping("/users/decreaseMoney")
    ApiResult<Object> decreaseMoney(Long id, Integer price);

}
