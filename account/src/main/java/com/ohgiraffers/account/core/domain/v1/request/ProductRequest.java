package com.ohgiraffers.account.core.domain.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private Long categoryId;
    private Long adminId;
}