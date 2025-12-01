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
    private String category;

    @Column(name = "admin_id", nullable = false)
    private Long adminId; // FIX: ManyToOne 연관 관계를 제거하고 Long 타입의 adminId 필드 사용

    // 생성자 (등록 시 사용)
    public Product(String name, String description, String imageUrl, Integer price, String category, Long adminId) { // FIX: Admin -> Long adminId로 변경
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.adminId = adminId;
    }

    // 수정 메서드
    public void update(String name, String description, Integer price, String imageUrl, String category, Long adminId) { // FIX: Admin -> Long adminId로 변경
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