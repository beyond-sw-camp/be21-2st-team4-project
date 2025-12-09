package com.ohgiraffers.product.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.queue.core.api.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "timedeal-account-service", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/promotions/{userId}/grade")
    ApiResult<String> getUserGrade(@PathVariable("userId") Long userId);
}
