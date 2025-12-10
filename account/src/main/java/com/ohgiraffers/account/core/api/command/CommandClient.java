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
    public List<MyPageOrderResponse> getMeOrders(Long userId) {
        ApiResult<OrderDetailResponse> response = orderClient.getMeOrders(userId);
        if(response.getResult() == ResultType.ERROR) {
            return null;
        }
        return response.getData().myPageOrderResponseList();
    }
}