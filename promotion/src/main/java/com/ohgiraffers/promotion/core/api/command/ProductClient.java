package com.ohgiraffers.promotion.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.promotion.core.api.controller.v1.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "timedeal-product-service", configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface ProductClient {

    @GetMapping("/{id}")
    ApiResult<ProductResponse> findById(@PathVariable("id") Long id);
}
