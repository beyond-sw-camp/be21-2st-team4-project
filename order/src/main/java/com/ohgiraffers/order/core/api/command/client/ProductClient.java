package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.ProductResponse;
import com.ohgiraffers.order.core.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "timedeal-product-service", configuration = FeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/products/{id}")
    ApiResult<ProductResponse> getProduct(Long id);
}
