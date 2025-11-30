package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import java.time.LocalDateTime;

public record MyPageOrderResponse(
        Long orderId,       // 주문 번호
        String image,       // 상품 이미지
        String itemName,    // 상품 이름
        Integer quantity,   // 개수
        Integer price,       // 가격(=subtotal)
        LocalDateTime orderDate  // 주문 날짜
){

}