package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }
}
