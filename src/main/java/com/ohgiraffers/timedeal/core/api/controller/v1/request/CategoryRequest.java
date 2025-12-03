package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    private String name;      // 카테고리 이름
    private String status;    // "ACTIVE" 또는 "DELETED"
}