package com.ohgiraffers.order.core.api.command.common;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.common.support.response.ResultType;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ApiResultHandler {

    public <T> T unwrap(ApiResult<T> result, Supplier<RuntimeException> exceptionSupplier) {
        if (!ResultType.SUCCESS.equals(result.getResult())) {
            throw exceptionSupplier.get();
        }
        return result.getData();
    }
}

