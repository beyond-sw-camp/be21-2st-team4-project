package com.ohgiraffers.order.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.order.core.domain.OrderService;
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
