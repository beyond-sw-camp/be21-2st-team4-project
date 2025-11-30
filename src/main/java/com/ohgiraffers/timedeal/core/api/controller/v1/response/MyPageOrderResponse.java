package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "마이페이지 응답")
public record MyPageOrderResponse(
        @Schema(description = "주문 번호" )
        Long orderId,       // 주문 번호
        @Schema(description = "상품 이미지")
        String image,       // 상품 이미지
        @Schema(description = "상품 이름")
        String PromotionName,    // 상품 이름
        @Schema(description = "개수")
        Integer quantity,   // 개수
        @Schema(description = "가격")
        Integer price,       // 가격(=subtotal)
        @Schema(description = "주문날짜")
        LocalDateTime orderDate  // 주문 날짜
){

}