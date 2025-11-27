package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.ProductRepository;
import com.ohgiraffers.timedeal.storage.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public UserService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    public Boolean signIn(String email, String password) {
        boolean result = userRepository.existsUsernameAndPassword(email, password);
        if (!result)
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        return result;
    }

    public Boolean signUp(String email, String password, String name) {
        // 1. 이메일 중복 검사
        if (!userRepository.existsByEmail(email)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        // 2. User 생성 후 저장
        User user = new User(email, password, name);
        userRepository.save(user);
        return true;

    }

    public MyPageResponse getMyPage(MyPageRequest request) {
        User user = userRepository.findByEmail(email);
        List<product> products = orderRepository.findByUserEmail(email);
        int couponCount = couponRepository.countByUserEmail(email);
        return new MyPageResponse(user, orders, couponCount);

    }
}
