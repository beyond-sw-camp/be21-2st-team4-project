package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.product.ProductReader;
import com.ohgiraffers.order.core.api.command.promotion.PromotionReader;
import com.ohgiraffers.order.core.api.command.promotion.PromotionValidator;
import com.ohgiraffers.order.core.api.command.queue.QueueReader;
import com.ohgiraffers.order.core.api.command.user.UserReader;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.order.storage.OrderDetailRepository;
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
    private final PromotionReader promotionReader;
    private final PromotionValidator promotionValidator;
    private final UserReader userReader;
    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final QueueReader queueReader;

    @Transactional
    public void createOrder(Long userId, OrderRequest orderRequest) {
        
        // 요청 유효성 검증
        orderRequest.validate();

        RLock lock = redissonClient.getLock("lock:stock:" + orderRequest.getPromotionId());

        try {
            if (!lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            var user = userReader.getUser(userId);
            var promotion = promotionReader.getPromotion(orderRequest.getPromotionId());

            // 대기열 통과 검증
            queueReader.verify(promotion.id(), user.id());

            // 프로모션 유효성 체크(상태, 재고)
            promotionValidator.validate(promotion, orderRequest.getQuantity());

            // 프로모션 재고 차감
            promotionReader.decrease(orderRequest);

            // 유저 잔액 차감
            userReader.decreaseMoney(user.id(), promotion.salePrice());

            // 대기열 완료 처리
            boolean completed = queueReader.complete(promotion.id(), user.id());
            if (!completed) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            // Order 생성
            Order order = Order.create(user.id());
            // 주문 금액 계산
            var totalAmount = order.calPrice(promotion.salePrice(), orderRequest.getQuantity());

            // Order 저장
            orderRepository.save(order);

            // OrderDetail 생성
            OrderDetail detail = OrderDetail.addDetail(order, promotion, orderRequest);

            // OrderDetail 저장
            orderDetailRepository.save(detail);

        } catch (InterruptedException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        } finally {
            lock.unlock();
        }
    }
}