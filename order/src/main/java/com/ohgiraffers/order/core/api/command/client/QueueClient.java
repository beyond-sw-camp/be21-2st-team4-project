package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "timedeal-queue-service", configuration = FeignClientConfig.class)
public interface QueueClient {

    @GetMapping("/queues/verify")
    ApiResult<Object> verifyQueue(
            @RequestParam Long timedealId,
            @RequestParam Long userId);
}
