package com.ohgiraffers.order.core.api.command.promotion;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import org.springframework.stereotype.Component;

@Component
public class PromotionValidator {

    public void validate(PromotionResponse p) {

        // 상태 검증
        switch(p.promotionStatus()) {

            case ACTIVE -> {}
            case ENDED
                -> throw new CoreException(ErrorType.DEFAULT_ERROR);
            case SCHEDULER
                -> throw new CoreException(ErrorType.DEFAULT_ERROR);
            default
                -> throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
