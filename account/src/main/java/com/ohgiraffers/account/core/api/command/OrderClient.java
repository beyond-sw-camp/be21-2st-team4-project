package com.ohgiraffers.account.core.api.command;

import com.ohgiraffers.account.core.api.config.FeignClientConfig;
import com.ohgiraffers.account.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.common.support.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "timedeal-order-service" , configuration = FeignClientConfig.class)
public interface OrderClient {

    @GetMapping("/me/orders")
    ApiResult<OrderDetailResponse> getMeOrders(
            @RequestParam Long userId
    );
}