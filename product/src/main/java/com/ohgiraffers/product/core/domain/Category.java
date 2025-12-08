package com.ohgiraffers.product.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import com.ohgiraffers.common.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }

    public void changeStatus(EntityStatus status) {
        super.deleted();
        this.active();
    }

}