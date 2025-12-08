package com.ohgiraffers.account.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "admins")
public class Admin extends BaseEntity {

    @Column(name="email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name="password", nullable = false, length = 250)
    private String password;

    @Column(name="company", nullable = false, length = 100)
    private String company;

    // 생성자 (등록 시 사용)
    public Admin(String email, String password, String company) {
        this.email = email;
        this.password = password;
        this.company = company;
    }

    // 수정 메서드
    public void update(String email, String password, String company) {
        this.email = email;
        this.password = password;
        this.company = company;
    }
}