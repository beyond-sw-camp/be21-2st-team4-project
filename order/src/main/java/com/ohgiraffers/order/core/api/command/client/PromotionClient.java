package com.ohgiraffers.order.core.api.command.client;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient
public interface PromotionClient {

    ApiResult<PromotionResponse> getPromotion(Long id);
}
