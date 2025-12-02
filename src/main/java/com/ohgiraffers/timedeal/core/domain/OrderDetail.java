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
@Table(name = "order_details")
public class OrderDetail extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;

    @Column(name = "promotion_name", length = 255, nullable = false)
    private String promotionName;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    public static OrderDetail of(Long orderId, Long promotionId, Integer quantity, Integer unitPrice) {
        OrderDetail detail = new OrderDetail();
        detail.orderId = orderId;
        detail.promotionId = promotionId;
        detail.quantity = quantity;
        detail.unitPrice = unitPrice.doubleValue();
        detail.subtotal = unitPrice* quantity;
        return detail;
    }


}
