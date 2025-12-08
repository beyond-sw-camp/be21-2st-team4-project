package com.ohgiraffers.account.core.domain;

import com.ohgiraffers.account.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.account.storage.UserRepository;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;



import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;


    //로그인
    public SignInResponse signIn(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        String token = UUID.randomUUID().toString();
        saveToken(user.getId(),token,3600);
        return new SignInResponse(user.getId(),token);

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

    public Boolean verifyToken(Long userId, String token) {
        String getToken = redisTemplate.opsForValue().get("UserToken:" + userId);
        return (getToken!= null && getToken.equals(token));
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

//    public OrderDetailResponse getMeOrders(Long userId) {
//        List<Order> orders = orderRepository.findByUserId(userId); // 병합 필요
//
//        List<MyPageOrderResponse> myPageOrders = new ArrayList<>();
//        for (Order order : orders) {
//            OrderDetail detail = orderDetailRepository.findByOrderId(order.getId())
//                    .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
//            MyPageOrderResponse response = new MyPageOrderResponse(
//                    order.getId(),
//                    detail.getImageUrl(),
//                    detail.getPromotionName(),
//                    detail.getQuantity(),
//                    detail.getSubtotal(),
//                    order.getCreatedAt()
//            );
//            myPageOrders.add(response);
//        }
//        return new OrderDetailResponse(myPageOrders);
//    }

}
