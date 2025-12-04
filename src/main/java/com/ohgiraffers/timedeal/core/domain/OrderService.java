package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.OrderDetailRepository;
import com.ohgiraffers.timedeal.storage.OrderRepository;
import com.ohgiraffers.timedeal.storage.ProductRepository;
import com.ohgiraffers.timedeal.storage.PromotionRepository;
import com.ohgiraffers.timedeal.storage.UserRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

        // 요청 유효성 검증
        orderRequest.validate();

        RLock lock = redissonClient.getLock("lock:stock:" + orderRequest.getPromotionId());

        try {
            if (!lock.tryLock(5, 1, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            // 상품 조회
            Promotion promotion = promotionRepository.findById(orderRequest.getPromotionId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            // 유저 조회
            User user = userRepository.findById(orderRequest.getUserId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            // 프로모션 상태 체크
            if (promotion.getPromotionStatus() != PromotionStatus.ACTIVE) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            // 유저 잔액 체크
            if (user.getMoney() < promotion.getSalePrice()) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            // Redis로 재고 감소
            boolean stockAvailable = stockService.decreaseStock(
                    orderRequest.getPromotionId(),
                    orderRequest.getQuantity()
            );

            if(!stockAvailable){
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            // 구매 처리
            user.decreaseMoney(promotion.getSalePrice().longValue());     // 유저 잔액 차감
            promotion.increaseSoldQuantity();             // soldQuantity += 1

            // Order 생성
            Integer totalAmount = promotion.getSalePrice().intValue() * orderRequest.getQuantity();
            Order order = Order.create(user.getId(), totalAmount);
            orderRepository.save(order);

            Product product = productRepository.findById(promotion.getProductId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

            // OrderDetail 생성
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
