package com.ohgiraffers.queue.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.queue.core.api.config.FeignClientConfig;
import com.ohgiraffers.queue.core.api.controller.v1.response.PromotionListResponse;
import com.ohgiraffers.queue.core.api.controller.v1.response.PromotionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "timedeal-promotion-service", configuration = FeignClientConfig.class)
public interface PromotionClient {

    @GetMapping("/api/v1/promotions/{id}")
    ApiResult<PromotionResponse> getPromotion(@PathVariable long id);

    @GetMapping("/api/v1/promotions/{promotionStatus}")
    ApiResult<PromotionListResponse> getActivePromotions(@PathVariable String promotionStatus);
}
