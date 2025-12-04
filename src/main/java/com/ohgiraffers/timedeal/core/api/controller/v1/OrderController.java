package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderResponse;
import com.ohgiraffers.timedeal.core.domain.OrderService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary="주문 생성")
    @PostMapping
    public ApiResult<?> createOrder(@RequestBody @Valid OrderRequest request) {
        orderService.createOrder(request);
        return ApiResult.success();
    }


}
