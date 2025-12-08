package com.ohgiraffers.order.storage;

import com.ohgiraffers.order.core.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(Long userId);
}
