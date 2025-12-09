package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.account.core.domain.User;
import com.ohgiraffers.account.storage.UserRepository;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.product.core.domain.Product;
import com.ohgiraffers.product.storage.ProductRepository;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import com.ohgiraffers.promotion.storage.PromotionRepository;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.storage.OrderDetailRepository;
import com.ohgiraffers.order.storage.OrderRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RedissonClient redissonClient;
    private final StockService stockService;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            PromotionRepository promotionRepository,
            UserRepository userRepository,
            OrderDetailRepository orderDetailRepository,
            RedissonClient redissonClient,
            StockService stockService,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.promotionRepository = promotionRepository;
        this.userRepository = userRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.redissonClient = redissonClient;
        this.stockService = stockService;
        this.productRepository = productRepository;
    }

    @Transactional
    public void createOrder(OrderRequest orderRequest) {
        orderRequest.validate();

        RLock lock = redissonClient.getLock("lock:stock:" + orderRequest.getPromotionId());

        try {
            if (!lock.tryLock(5, 1, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            Promotion promotion = promotionRepository.findById(orderRequest.getPromotionId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            User user = userRepository.findById(orderRequest.getUserId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            stockService.validProcessedQueue(orderRequest.getUserId());
            stockService.validCompleteQueue(orderRequest.getPromotionId(), orderRequest.getUserId());

            if (promotion.getPromotionStatus() != PromotionStatus.ACTIVE) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            if (user.getMoney() < promotion.getSalePrice()) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            boolean stockAvailable = stockService.decreaseStock(
                    orderRequest.getPromotionId(),
                    orderRequest.getQuantity()
            );

            if (!stockAvailable) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            stockService.deleteProcessedQueue(orderRequest.getUserId());
            stockService.addCompleteQueue(orderRequest.getPromotionId(), orderRequest.getUserId());

            user.decreaseMoney(promotion.getSalePrice().longValue());
            promotion.increaseSoldQuantity();

            Integer totalAmount = promotion.getSalePrice().intValue() * orderRequest.getQuantity();
            Order order = Order.create(user.getId(), totalAmount);
            orderRepository.save(order);

            Product product = productRepository.findById(promotion.getProductId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            OrderDetail orderDetail = OrderDetail.of(
                    order.getId(),
                    promotion.getId(),
                    orderRequest.getQuantity(),
                    promotion.getSalePrice().intValue(),
                    product.getImageUrl(),
                    product.getName()
            );
            orderDetailRepository.save(orderDetail);

        } catch (InterruptedException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        } finally {
            lock.unlock();
        }
    }
}