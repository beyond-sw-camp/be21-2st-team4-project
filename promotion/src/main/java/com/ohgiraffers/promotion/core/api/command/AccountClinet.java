package com.ohgiraffers.promotion.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.promotion.core.api.controller.v1.response.AdminResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "timedeal-account-service", configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface AccountClinet {

    @GetMapping("/admin/{id}")
    ApiResult<AdminResponse> findAdminById(@PathVariable Long id);
}
