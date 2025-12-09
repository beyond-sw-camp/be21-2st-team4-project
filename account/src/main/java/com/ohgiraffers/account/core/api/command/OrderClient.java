package com.ohgiraffers.account.core.api.command;

import com.ohgiraffers.account.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.common.support.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "timedeal-order-service")
public interface OrderClient {

    @GetMapping("/api/v1/orders/me/orders")
    ApiResult<OrderDetailResponse> getMeOrders(
            @RequestParam Long userId,
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestHeader("Authorization") String authorization
    );
}