package com.ohgiraffers.promotion.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.promotion.core.api.command.ProductClient;
import com.ohgiraffers.promotion.core.api.controller.v1.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandClient {
    private final ProductClient productClient;

    public ProductResponse getProduct(Long id) {
        ApiResult<ProductResponse> response = productClient.getProduct(id);
        if(response.getResult() == ResultType.ERROR) {
            return null;
        }
        return response.getData();
    }

}
