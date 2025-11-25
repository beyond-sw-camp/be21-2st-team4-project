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
@AllArgsConstructor
@Table(name = "examples")

public class User extends BaseEntity {
    @Column(name="email",length=10,nullable = false,unique=true)
    private String email;

    @Column(name="password",length=10,nullable = false)
    private String password;

    @Column(name="money")
    private int money = 0;

    @Column(name="total_saved")
    private int total_saved =0;
}