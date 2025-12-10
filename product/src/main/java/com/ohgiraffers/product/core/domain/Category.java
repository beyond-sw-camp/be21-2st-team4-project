package com.ohgiraffers.product.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    // 카테고리명 수정
    public void update(String name) {
        this.name = name;
    }
}