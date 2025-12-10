package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import com.ohgiraffers.order.core.api.command.response.ProductResponse;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus orderStatus = OrderStatus.DONE;

    public static Order create(Long userId) {
        Order order = new Order();
        order.userId = userId;
        order.orderStatus = OrderStatus.DONE;
        return order;
    }

    @OneToMany
    private List<OrderDetail> details = new ArrayList<>();

    public void addDetail(PromotionResponse promotion, ProductResponse product, Integer quantity) {

        OrderDetail detail = OrderDetail.of(
                this.getId(),
                promotion.id(),
                quantity,
                promotion.salePrice(),
                product.imageUrl(),
                product.name()
        );

        this.details.add(detail);
        this.totalAmount += promotion.salePrice() * quantity;
    }

}
