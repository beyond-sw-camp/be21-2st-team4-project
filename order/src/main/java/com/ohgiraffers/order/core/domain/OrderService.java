package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.product.ProductReader;
import com.ohgiraffers.order.core.api.command.promotion.PromotionReader;
import com.ohgiraffers.order.core.api.command.promotion.PromotionValidator;
import com.ohgiraffers.order.core.api.command.user.UserReader;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.order.storage.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PromotionReader promReader;
    private final PromotionValidator promValidator;
    private final ProductReader prodReader;
    private final UserReader userReader;
    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;

    @Transactional
    public void createOrder(OrderRequest orderRequest) {

        // 요청 유효성 검증
        orderRequest.validate();

        RLock lock = redissonClient.getLock("lock:stock:" + orderRequest.getPromotionId());

        try {
            if (!lock.tryLock(5, 1, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            var user = userReader.getUser(orderRequest.getUserId());
            var promotion = promReader.getPromotion(orderRequest.getPromotionId());
            var product = prodReader.getProduct(promotion.productId());

            // 프로모션 상태 체크
            promValidator.validate(promotion);

            // Order 생성
            Order order = Order.create(user.id());

            // OrderDetail 생성
            order.addDetail(promotion, product, orderRequest.getQuantity());

            // OrderDetail 저장
            orderRepository.save(order);

        } catch (InterruptedException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        } finally {
            lock.unlock();
        }
    }
}