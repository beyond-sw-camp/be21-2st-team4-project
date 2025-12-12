package com.ohgiraffers.promotion.core.domain;

import com.ohgiraffers.common.entity.BaseEntity;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotions")
public class Promotion extends BaseEntity {

    @Column(name = "admin_id" ,  nullable = false)
    private Long adminId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "sale_price")
    private Integer salePrice;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "sold_quantity" ,  nullable = false)
    private Integer soldQuantity = 0;

    @Column(name = "promotion_status")
    @Enumerated(EnumType.STRING)
    private PromotionStatus promotionStatus = PromotionStatus.SCHEDULER;


    public Promotion(Long adminId, Long productId, Integer discountRate, LocalDateTime startTime, LocalDateTime endTime, Integer totalQuantity) {
        this.adminId = adminId;
        this.productId = productId;
        this.discountRate = discountRate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuantity = totalQuantity;
    }

    public void changeStatus(PromotionStatus promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public void updatePromotion(Long adminId,
                                Long productId,
                                Integer discountRate,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                Integer totalQuantity) {

        this.adminId = adminId;
        this.productId = productId;
        this.discountRate = discountRate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuantity = totalQuantity;
    }

    public void decreaseSoldQuantity(Integer quantity) {
        if(this.soldQuantity + quantity > this.totalQuantity) {
            throw new CoreException(ErrorType.PROMOTION_SOLDQUANTITY_OVER);
        }
        this.soldQuantity += quantity;
    }

}
