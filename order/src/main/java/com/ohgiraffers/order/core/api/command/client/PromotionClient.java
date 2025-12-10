package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import com.ohgiraffers.order.core.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "timedeal-promotion-service", configuration = FeignClientConfig.class)
public interface PromotionClient {

    @GetMapping("/{id}")
    ApiResult<PromotionResponse> getPromotion(@PathVariable Long id);

    @GetMapping("/decrease")
    ApiResult<PromotionResponse> decrease(
            @RequestParam Long promotionId,
            @RequestParam Integer quantity
    );
}
