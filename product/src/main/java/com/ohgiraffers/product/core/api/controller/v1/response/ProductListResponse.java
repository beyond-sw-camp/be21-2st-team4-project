package com.ohgiraffers.product.core.api.controller.v1.response;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> products
) {

}