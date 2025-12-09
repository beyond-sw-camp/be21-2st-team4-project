package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient
public interface ProductClient {

    ApiResult<ProductResponse> getProduct(Long id);
}
