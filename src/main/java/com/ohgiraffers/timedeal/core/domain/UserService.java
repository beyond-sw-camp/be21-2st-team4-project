package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.config.RedisConfig;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageOrderResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.OrderDetailRepository;
import com.ohgiraffers.timedeal.storage.OrderRepository;
import com.ohgiraffers.timedeal.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //로그인
    public SignInResponse signIn(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        String token = UUID.randomUUID().toString();
        saveToken(user.getId(),token,3600);
        return new SignInResponse(token);

    }

    //로그아웃
    public void signOut(Long userId) {
        deleteToken(userId);
    }

    // 로그인 시 토큰 저장
    public void saveToken(Long userId, String token, long seconds) {
        String key = "UserToken:" + userId;
        redisTemplate.opsForValue().set(key, token, Duration.ofSeconds(seconds));
    }

    // 토큰 조회
    public String getToken(Long userId) {
        return redisTemplate.opsForValue().get("UserToken:" + userId);
    }

    // 로그아웃 시 토큰 삭제
    public void deleteToken(Long userId) {
        redisTemplate.delete("UserToken:" + userId);
    }

    // 회원가입
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
