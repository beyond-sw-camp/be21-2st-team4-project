package com.ohgiraffers.promotion.storage;

import com.ohgiraffers.promotion.core.api.controller.v1.response.OrderResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.promotion.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.promotion.core.domain.Promotion;
import com.ohgiraffers.promotion.core.enums.PromotionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {


    Optional<Promotion> findByProductId(Long productId);

    Promotion findPromotionById(Long id);

    List<Promotion> findAllByPromotionStatus(PromotionStatus promotionStatus);

    PromotionStatus findPromotionStatusById(Long id);

    List<Promotion> findAll();

    @Modifying
    @Query("""
    update Promotion 
    set promotionStatus = :status
    where id = :id
""")
    void updatePromotionStatus(Long id, PromotionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promotion p where p.id = :id")
    public Optional<Promotion> findByIdWithPessimisticLock(Long id);

    @Modifying
    @Query("""
    update Promotion
    set soldQuantity = :soldQuantity
    where id = :id
""")
    void updatePromotionSoldQuantity(Long id, int soldQuantity);

    int findSoldQuantityById(Long id);

    List<Promotion> findByPromotionStatusAndStartTimeBefore(PromotionStatus status, LocalDateTime time);
    List<Promotion> findByPromotionStatusAndEndTimeBefore(PromotionStatus status, LocalDateTime time);

    List<Promotion> findByPromotionStatus(PromotionStatus promotionStatus);
    //Promotion findById(Long id);
}