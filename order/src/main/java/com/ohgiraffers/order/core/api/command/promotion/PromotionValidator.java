package com.ohgiraffers.order.core.api.command.promotion;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.common.PromotionStatus;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import org.springframework.stereotype.Component;

@Component
public class PromotionValidator {

    public void validate(PromotionResponse p, Integer quantity) throws CoreException {

        // 상태 검증
        if(p.promotionStatus() != PromotionStatus.ACTIVE) {
            throw new CoreException(ErrorType.PROMOTION_STAUTS_INVALID);
        }

        // 재고 수량 검증
        if((p.totalQuantity() - p.soldQuantity()) <= quantity) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
