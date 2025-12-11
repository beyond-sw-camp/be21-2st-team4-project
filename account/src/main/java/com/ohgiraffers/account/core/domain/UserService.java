package com.ohgiraffers.account.core.domain;

import com.ohgiraffers.account.core.api.command.CommandClient;
import com.ohgiraffers.account.core.api.controller.v1.response.*;
import com.ohgiraffers.account.security.JwtTokenProvider;
import com.ohgiraffers.account.storage.UserRepository;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;


import com.ohgiraffers.common.support.response.ApiResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CommandClient commandClient;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    public SignInResponse signIn(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND_EMAIL));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CoreException(ErrorType.USER_NOT_EQUALS_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(
                user.getEmail(),     // username
                "USER",              // role (지금 User 엔티티에 role 없어서 고정)
                user.getId()
        );

        return new SignInResponse(user.getId(), accessToken);
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
        return (getToken != null && getToken.equals(token));
    }

    // 로그아웃 시 토큰 삭제
    public void deleteToken(Long userId) {
        redisTemplate.delete("UserToken:" + userId);
    }

    // 회원가입
    public void signUp(String email, String password, String name) {
        // 1. 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new CoreException(ErrorType.USER_EXISTS_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, name);
        userRepository.save(user);

    }

    public MyPageResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        return new MyPageResponse(
                user.getName(),
                user.getMoney(),
                user.getTotal_saved()
        );
    }
    @Transactional
    public OrderDetailResponse getMeOrders(Long userId) {
        List<MyPageOrderResponse> meOrders = commandClient.getMeOrders(userId);
        return new OrderDetailResponse(meOrders);
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        return new UserResponse(user.getId(), user.getEmail(), user.getMoney());
    }

    @Transactional
    public void decreaseMoney(Long userId, Integer price) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        user.decreaseMoney(price);
    }

}
