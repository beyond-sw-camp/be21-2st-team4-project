package com.ohgiraffers.order.storage;

import com.ohgiraffers.order.core.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {

    Optional<OrderDetail> findByOrderId(Long id);
}
