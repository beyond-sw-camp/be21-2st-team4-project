package com.ohgiraffers.order.core.api.command.product;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.common.ApiResultHandler;
import com.ohgiraffers.order.core.api.command.client.ProductClient;
import com.ohgiraffers.order.core.api.command.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductReader {

    private final ApiResultHandler apiResult;
    private final ProductClient client;

    // Product
    public ProductResponse getProduct(Long id) {

        return apiResult.unwrap(
                client.getProduct(id),
                () -> new CoreException(ErrorType.DEFAULT_ERROR)
        );
    }
}
