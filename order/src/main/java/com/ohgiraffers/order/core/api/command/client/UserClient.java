package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient
public interface UserClient {
    
    ApiResult<UserResponse> getUser(Long id);
}
