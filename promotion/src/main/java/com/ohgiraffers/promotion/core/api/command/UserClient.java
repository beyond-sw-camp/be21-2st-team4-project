package com.ohgiraffers.promotion.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "timedeal-account-service", configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface UserClient {

    @GetMapping("/account/{userId}/grade")
    ApiResult<String> getUserGrade(@PathVariable("userId") Long userId);
}
