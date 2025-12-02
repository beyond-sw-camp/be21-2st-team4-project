package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
// 💡 생성자 주입 및 Lombok 사용을 위해 AccessLevel.PROTECTED 설정
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "category",
        indexes = {
                // 💡 FIX: name 필드에 고유 인덱스 설정 (Example 양식 참고)
                @Index(name = "idx_category_name", columnList = "name", unique = true)
        }
)
public class Category extends BaseEntity {

    // 💡 FIX: category_name이 아닌 name 필드 사용
    @Column(name = "name", nullable = false)
    private String name;

    // 카테고리 생성자 (name만 받음)
    public Category(String name) {
        this.name = name;
    }
}