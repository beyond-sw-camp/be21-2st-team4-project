package com.ohgiraffers.order.core.api.controller.v1.request;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull
    @Schema(description = "프로모션 상품 Id")
    private Long promotionId;

    @NotNull
    @Positive
    @Schema(description = "구매 수량")
    private Integer quantity;

    public void validate() {
        if (promotionId == null) {
            throw new CoreException(ErrorType.PROMOTION_ID_INVALID);
        }
        if (quantity <= 0) {
            throw new CoreException(ErrorType.OUT_OF_STOCK);
        }
        if(quantity > 5) {
            throw new CoreException(ErrorType.QUANTITY_LIMIT_EXCEEDED);
        }
    }
}

