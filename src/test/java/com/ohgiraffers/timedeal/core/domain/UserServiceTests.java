package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.MyPageResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.timedeal.storage.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    StringRedisTemplate redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @InjectMocks
    UserService userService;

    @DisplayName("로그인 정상 반환")
    @Test
    void getSignIn() {
        //given
        String email = "test1@naver.com";
        String password = "Password1!";
        User user = new User(email,password,"홍길동");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByEmailAndPassword(email, password))
                .thenReturn(Optional.of(user));
        //when
        SignInResponse result = userService.signIn(email, password);
        //then
        Assertions.assertEquals(1L,result.userId());
        Assertions.assertNotNull(result.token());
    }

    @DisplayName("아이디 잘못 입력했을 경우 로그인")
    @Test
    void getFalseEmailSignIn() {
        //given
        String email1 = "test1@naver.com";
        String email2 = "test2@naver.com";
        String password = "Password1!";
        User user = new User(email1,password,"홍길동");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByEmailAndPassword(email1, password))
                .thenReturn(Optional.of(user));
        //when
        SignInResponse result = userService.signIn(email2, password);
        //then
        Assertions.assertEquals(1L,result.userId());
        Assertions.assertNotNull(result.token());
    }

    @DisplayName("비밀번호 잘못 입력했을 경우 로그인")
    @Test
    void getFalsePasswordSignIn() {
        //given
        String email = "test1@naver.com";
        String password1 = "Password1!";
        String password2 = "Password2!";
        User user = new User(email,password1,"홍길동");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByEmailAndPassword(email, password1))
                .thenReturn(Optional.of(user));
        //when
        SignInResponse result = userService.signIn(email, password2);
        //then
        Assertions.assertEquals(1L,result.userId());
        Assertions.assertNotNull(result.token());
    }

    @DisplayName("회원가입 정상 반환")
    @Test
    void getSignUn() {
        //given
        String email = "test1@naver.com";
        String password = "Password1!";
        String name = "홍길동";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        //when
        SignInResponse result = userService.signIn(email, password);
        //then
        Assertions.assertEquals(1L,result.userId());
        Assertions.assertNotNull(result.token());
    }

    @DisplayName("마이페이지 정상 반환")
    @Test
    void getMe(){
        //given
        Long id = 1L;
        User user = new User("홍길동",50000,10000);
        ReflectionTestUtils.setField(user, "id", id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        //when
        MyPageResponse test = userService.getMe(id);
        //then
        Assertions.assertEquals(test.name(),"홍길동");
        Assertions.assertEquals(test.money(),50000);
        Assertions.assertEquals(test.total_saved(),10000);
    }

    @DisplayName("마이페이지 실4패 반환")
    @Test
    void getFalseMe(){
        //given
        Long id1 = 1L;
        Long id2 = 2L;
        User user = new User("홍길동",50000,10000);
        ReflectionTestUtils.setField(user, "id", id1);
        when(userRepository.findById(id1)).thenReturn(Optional.of(user));
        //when
        MyPageResponse test = userService.getMe(id2);
        //then
        Assertions.assertEquals(test.name(),"홍길동");
        Assertions.assertEquals(test.money(),50000);
        Assertions.assertEquals(test.total_saved(),10000);
    }

    @DisplayName("마이페이지 주문목록 성공 반환")
    @Test
    void getMeOrders(){
        //given
        Long id = 1L;
        String email = "test1@naver.com";
        String password = "Password1!";
        User user = new User(email,password,"홍길동");
        ReflectionTestUtils.setField(user, "id", id);

        Order order = Order.create(1L, 5000);
        ReflectionTestUtils.setField(order, "id", 1L);

        when(orderRepository.findByUserId(id)).thenReturn(List.of(order));

        OrderDetail detail = OrderDetail.of(1L, 1L, 2, 1000);
        ReflectionTestUtils.setField(detail, "promotionName", "프로모션1");
        ReflectionTestUtils.setField(detail, "imageUrl", "test.jpg");

        when(orderDetailRepository.findByOrderId(order.getId())).thenReturn(Optional.of(detail));
        //when
        OrderDetailResponse test = userService.getMeOrders(id);
        //then
//        Assertions.assertEquals(order.getUserId(),user.getId()); //사용자가 주문한 id가 맞는지 확인
//        Assertions.assertEquals(order.getId(),detail.getOrderId()); // 주문 id와 detail.id가 맞는지 확인
        Assertions.assertEquals(test.myPageOrderResponseList().get(0).PromotionName(),"프로모션1");
        Assertions.assertEquals(test.myPageOrderResponseList().get(0).image(),"test.jpg");
        Assertions.assertEquals(test.myPageOrderResponseList().get(0).price(),2000);
    }



    @DisplayName("토큰 값 정상 반환")
    @Test
    void getTrueUserByToken() {
        // given
        Long userId = 1L;
        String token = "getUserByToken_token";
        String key = "UserToken:" + userId;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(token);

        // when
        Boolean verify = userService.verifyToken(userId, token);

        // then
        Assertions.assertTrue(verify);
    }

    @DisplayName("토큰 값 실패 반환")
    @Test
    void getFalseUserByToken() {
        //given
        Long userId = 1L;
        String token1 = "success_token";
        String token2 = "failure_token";
        when(valueOperations.get(userId)).thenReturn(token1);

        // when
        Boolean verify = userService.verifyToken(userId, token2);

        // then
        Assertions.assertTrue(verify);
    }




}