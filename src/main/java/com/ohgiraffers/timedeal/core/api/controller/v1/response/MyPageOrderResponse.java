package com.ohgiraffers.timedeal.core.api.controller.v1.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

//@Getter
//@AllArgsConstructor
//public class MyPageOrderResponse {
//    private Long orderId;       // 주문 번호
//    private String image;       // 상품 이미지
//    private String itemName;    // 상품 이름
//    private Integer quantity;   // 개수
//    private Double price;       // 가격(=subtotal)
//    private LocalDateTime orderDate;   // 주문 날짜
//}

public record MyPageOrderResponse(
        Long orderId,       // 주문 번호
        String image,       // 상품 이미지
        String itemName,    // 상품 이름
        Integer quantity,   // 개수
        Double price,       // 가격(=subtotal)
        LocalDateTime orderDate  // 주문 날짜
){

}