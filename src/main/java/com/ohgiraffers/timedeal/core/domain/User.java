package com.ohgiraffers.timedeal.core.domain;


import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import com.ohgiraffers.timedeal.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")

public class User extends BaseEntity {
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @Column(name = "money")
    private int money = 100000;

    @Column(name = "name")
    private String name;

    @Column(name="total_saved")
    private int total_saved =0;

    public User(String name, int money, int total_saved){
        this.name = name;
        this.money = money;
        this.total_saved = total_saved;
    }

    public User (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public void decreaseMoney(Long salePrice) {
        if(this.money < salePrice) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        this.money -= salePrice;
    }
}