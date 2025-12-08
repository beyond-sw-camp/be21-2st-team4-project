package com.ohgiraffers.queue.core.api.controller.v1.response;

import java.util.List;

public record PromotionListResponse(
        List<PromotionResponse> promotions
) {
    
}
