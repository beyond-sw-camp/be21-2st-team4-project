package com.ohgiraffers.order.core.api.command.promotion;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.common.ApiResultHandler;
import com.ohgiraffers.order.core.api.command.client.PromotionClient;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionReader {

    private final PromotionClient client;
    private final ApiResultHandler apiResult;

    // 프로모션
    public PromotionResponse getPromotion(Long id) {

        return apiResult.unwrap(
                client.getPromotion(id),
                () -> new CoreException(ErrorType.DEFAULT_ERROR)
        );
    }
}
