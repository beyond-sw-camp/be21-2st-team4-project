package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private Integer productid;
    private String Name;
    private String description;
    private Integer price;
    private String imageUrl; // 파일 업로드용
    private String category;
    private Long adminId;
}