package com.ohgiraffers.promotion.core.api.controller.v1.response;

import java.util.List;

public record PromotionListResponse(
        List<PromotionResponse> promotions
) {
    
}
