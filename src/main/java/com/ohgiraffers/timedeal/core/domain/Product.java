package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "category", length = 255, nullable = false)
    private String category; // Category 객체 대신 String category 유지

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    public Product(String name, String description, String imageUrl, Integer price, String category, Long adminId) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.adminId = adminId;
    }

    public void update(String name, String description, Integer price, String imageUrl, String category, Long adminId) {
        this.name = name;
        this.description = description;
        this.price = price;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
        this.category = category;
        this.adminId = adminId;
    }
}