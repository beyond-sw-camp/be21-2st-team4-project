package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.domain.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }
}
