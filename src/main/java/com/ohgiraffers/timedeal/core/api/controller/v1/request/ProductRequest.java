package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private Integer productid;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private String category;
    private Long adminId;
}