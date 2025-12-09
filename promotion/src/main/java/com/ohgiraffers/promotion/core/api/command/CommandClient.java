package com.ohgiraffers.promotion.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.promotion.core.api.command.ProductClient;
import com.ohgiraffers.promotion.core.api.command.UserClient;
import com.ohgiraffers.promotion.core.api.controller.v1.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandClient {
    private final ProductClient productClient;
    private final UserClient userClient;

    public ApiResult<ProductResponse> getProducts(Long id) {
        return ApiResult.success(productClient.getProduct(id).getData());
    }

    public boolean isValidUser(Long userId) {
        ApiResult<String> response = userClient.getUserGrade(userId);
        if (response.getResult() == ResultType.ERROR) {
            return false;
        }

        if (response.getData().equals("ADMIN")) {
            return false;
        }

        return true;
    }
}
