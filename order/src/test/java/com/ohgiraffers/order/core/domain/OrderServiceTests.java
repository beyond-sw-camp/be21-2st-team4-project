package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.order.core.api.command.promotion.PromotionReader;
import com.ohgiraffers.order.core.api.command.promotion.PromotionValidator;
import com.ohgiraffers.order.core.api.command.queue.QueueValidator;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import com.ohgiraffers.order.core.api.command.response.UserResponse;
import com.ohgiraffers.order.core.api.command.user.UserReader;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
import com.ohgiraffers.order.storage.OrderDetailRepository;
import com.ohgiraffers.order.storage.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private PromotionReader promotionReader;

    @Mock
    private PromotionValidator promotionValidator;

    @Mock
    private UserReader userReader;

    @Mock
    private QueueValidator queueValidator;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private RLock lock;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() throws Exception {

        // given
        Long userId = 1L;
        Long promotionId = 10L;
        OrderRequest request = new OrderRequest(promotionId, 2); // quantity = 2

        // Redisson Lock
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);

        // UserResponse mock
        UserResponse mockUser = new UserResponse(
                1L,
                "test@email.com",
                50000 // 충분한 잔액
        );
        when(userReader.getUser(userId)).thenReturn(mockUser);

        // PromotionResponse mock
        PromotionResponse mockPromotion = new PromotionResponse(
                promotionId,
                "companyA",
                "productName",
                20000,
                10000,            // salePrice
                0.5,
                100,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "img-url",
                null              // PromotionStatus 필요 없으므로 null
        );
        when(promotionReader.getPromotion(promotionId)).thenReturn(mockPromotion);

        // when
        orderService.createOrder(userId, request);

        // then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailRepository, times(1)).save(any(OrderDetail.class));

        verify(promotionReader, times(1)).decrease(request);
        verify(userReader, times(1)).decreaseMoney(mockUser.id(), mockPromotion.salePrice());
        verify(queueValidator, times(1)).complete(mockPromotion.id(), mockUser.id());

        verify(lock, times(1)).unlock();
    }
}
