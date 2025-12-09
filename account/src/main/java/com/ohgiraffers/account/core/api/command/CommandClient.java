package com.ohgiraffers.account.core.api.command;


import com.ohgiraffers.account.core.api.controller.v1.response.MyPageOrderResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.common.support.response.ResultType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandClient {

    private final OrderClient orderClient;
    private final HttpServletRequest request; // 현재 요청 주입

    public List<MyPageOrderResponse> getMeOrders(Long userId) {
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String authorization = request.getHeader("Authorization");

        ApiResult<OrderDetailResponse> response = orderClient.getMeOrders(
                userId,
                userIdHeader,
                roleHeader,
                authorization
        );

        if (response.getResult() == ResultType.ERROR) {
            return null;
        }
        return response.getData().myPageOrderResponseList();
    }
}