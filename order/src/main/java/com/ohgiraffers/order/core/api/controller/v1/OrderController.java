package com.ohgiraffers.order.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.Order.OrderReader;
import com.ohgiraffers.order.core.api.command.response.OrderDetailResponse;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.order.core.domain.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderReader orderReader;

    @Operation(summary="주문 생성")
    @PostMapping
    public ApiResult<?> createOrder(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid OrderRequest request) {
        orderService.createOrder(Long.parseLong(userId), request);
        return ApiResult.success();
    }

    @GetMapping("/me/orders")
    public ApiResult<OrderDetailResponse> getMeOrders(@AuthenticationPrincipal String userId) {
        return ApiResult.success(orderReader.getMeOrders(Long.parseLong(userId)));
    }
}
