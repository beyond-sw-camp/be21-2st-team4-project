package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageOrderResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.OrderDetailRepository;
import com.ohgiraffers.timedeal.storage.OrderRepository;
import com.ohgiraffers.timedeal.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public void signIn(String email, String password) {
        boolean result = userRepository.existsByEmailAndPassword(email, password);
        if (!result)
            throw new CoreException(ErrorType.DEFAULT_ERROR);
    }

    public void signUp(String email, String password, String name) {
        // 1. 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        // 2. User 생성 후 저장
        User user = new User(email, password, name);
        userRepository.save(user);

    }

    public MyPageResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        return new MyPageResponse(
                user.getName(),
                user.getMoney(),
                user.getTotal_saved()
        );
    }

    public OrderDetailResponse getMeOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId); // 병합 필요

        List<MyPageOrderResponse> myPageOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderDetail detail = orderDetailRepository.findByOrderId(order.getId())
                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
            MyPageOrderResponse response = new MyPageOrderResponse(
                    order.getId(),
                    detail.getImageUrl(),
                    detail.getPromotionName(),
                    detail.getQuantity(),
                    detail.getSubtotal(),
                    order.getCreatedAt()
            );
            myPageOrders.add(response);
        }
        return new OrderDetailResponse(myPageOrders);
    }

}
