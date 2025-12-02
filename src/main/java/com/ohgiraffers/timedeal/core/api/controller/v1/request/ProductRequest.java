package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private Integer productid;
    private String name; // Name -> name (Lombok 관례상 소문자로 변경)
    private String description;
    private Integer price;
    private String imageUrl; // 파일 업로드용
    // 💡 FIX: String category -> Long categoryId로 변경
    private Long categoryId;
    private Long adminId;
}