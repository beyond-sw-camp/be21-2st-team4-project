package com.ohgiraffers.order.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import com.ohgiraffers.order.core.api.command.response.PromotionResponse;
import com.ohgiraffers.order.core.api.controller.v1.request.OrderRequest;
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
    private Integer unitPrice;

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;

    @Column(name = "promotion_name", length = 255, nullable = false)
    private String promotionName;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public static OrderDetail addDetail(Order order,  PromotionResponse promotion
            , OrderRequest request) {
        OrderDetail detail = new OrderDetail(
                order.getId(),
                promotion.id(),
                request.getQuantity(),
                promotion.salePrice(),
                order.getTotalAmount(),
                promotion.productImageUrl(),
                promotion.productName()
        );

        return detail;
    }
}
