package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageOrderResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.OrderDetailResponse;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    @Autowired
    public UserService(UserRepository userRepository, OrderRepository orderRepository,OrderDetailRepository orderDetailRepository,PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }
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

    // 1. 마이페이지 + 주문 내역
//    public MyPageResponse getMyPage(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
//        List<Order> orders = orderRepository.findByUserId(userId);
//        List<MyPageOrderResponse> myPageOrders = new ArrayList<>();
//        for (Order order : orders) {
//            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
//            for (OrderDetail detail : details) {
//                // 예: promotionId로 Promotion 정보 조회 (상품명/이미지 가져오는 용도)
//                Promotion promotion = promotionRepository.findById(detail.getPromotionId())
//                        .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
//                MyPageOrderResponse response = new MyPageOrderResponse(
//                        order.getId(),
//                        "",//promotion.getImageUrl(),
//                        "", //promotion.getName(),
//                        detail.getQuantity(),
//                        detail.getSubtotal(),
//                        order.getCreatedAt()
//                );
//                myPageOrders.add(response);
//            }
//        }
//        return new MyPageResponse(user, myPageOrders);
//    }

    // 2. 마이페이지만
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
            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId()); // 병합 필요
            for (OrderDetail detail : details) {

                List<Promotion> promotions = promotionRepository.findByProductId(detail.getPromotionId()); //Repository에 메소드가 없는데 오류 x

                for(Promotion promotion : promotions){
                    product product = productRepository.findById(promotion.getProductId())
                            .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

                    MyPageOrderResponse response = new MyPageOrderResponse(
                            order.getId(),
                            "",//product.getImageUrl(),
                            "", //product.getName(),
                            detail.getQuantity(),
                            detail.getSubtotal(),   //double형으로 바꿈
                            order.getCreatedAt()
                    );

                    myPageOrders.add(response);
                }
            }
        }
        return new OrderDetailResponse(myPageOrders);
    }

}
