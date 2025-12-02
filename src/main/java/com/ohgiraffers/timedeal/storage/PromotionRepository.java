package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.RedisPromotionResponse;
import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {



    Optional<Promotion>findByProductId(Long productId);

    List<Promotion> findAllByPromotionStatus(PromotionStatus status);

    List<Promotion> findAll();

    List<RedisPromotionResponse> findPromotionIdAndTotalQuantityBYPromotionStatusisSCHEDULE();
    List<RedisPromotionResponse> findPromotionIdAndTotalQuantityBYPromotionStatusisACTIVE();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promotion p where p.id = :id")
    public Optional<Promotion> findByIdWithPessimisticLock(Long id);

}
