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

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    // 💡 FIX: Category와 ManyToOne 관계 설정 (category_id 컬럼 생성)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 생성자 (등록 시 사용)
    // 💡 FIX: String category -> Category category로 변경
    public Product(String name, String description, String imageUrl, Integer price, Category category, Long adminId) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.adminId = adminId;
    }

    // 💡 FIX: String category -> Category category로 변경
    public void update(String name, String description, Integer price, String imageUrl, Category category, Long adminId) {
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