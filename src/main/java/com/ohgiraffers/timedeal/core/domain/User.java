package com.ohgiraffers.timedeal.core.domain;


import com.ohgiraffers.timedeal.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")

public class User extends BaseEntity {
    @Column(name = "email", length = 10, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 10, nullable = false)
    private String password;

    @Column(name = "money")
    private int money = 100000;

    @Column(name = "name")
    private String name;


    public User (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Column(name="total_saved")
    private int total_saved =0;

    public void decreaseMoney(Long salePrice) {
        if(this.money < salePrice) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.money -= salePrice;
    }
}