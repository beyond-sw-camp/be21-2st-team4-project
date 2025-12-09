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

    @NotNull
    @Schema(description = "구매자 Id")
    private Long userId;

    public void validate() {
        if (promotionId == null) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        if (quantity <= 0) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        if(quantity > 5) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}

