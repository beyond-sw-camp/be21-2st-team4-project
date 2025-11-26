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
@Table(name = "order_detail")
public class OrderDetail extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private int orderId;

    @Column(name = "promotion_id", nullable = false)
    private int promotionId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "subtotal", nullable = false)
    private double subtotal;
}
