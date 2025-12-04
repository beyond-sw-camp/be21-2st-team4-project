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
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {



    Optional<Promotion>findByProductId(Long productId);

    Promotion findPromotionById(Long id);

    @Query("""
    select new com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse(
         pm.id
        ,pm.adminId
        ,pm.productId
        ,pm.salePrice
        ,pm.discountRate
        ,pm.totalQuantity
        ,pm.startTime
        ,pm.endTime
        ,pd.name
        ,pd.imageUrl
        ,pd.price)
    from Promotion pm
    join Product pd on pm.productId = pd.id
    where pm.promotionStatus = ?1
""")
    List<PromotionResponse> findByPromotionStatus(PromotionStatus promotionStatus);

    @Query(
            """
    select new com.ohgiraffers.timedeal.core.api.controller.v1.response.RedisPromotionResponse(
        p.id,
        p.totalQuantity)
       from Promotion p
       where p.promotionStatus = ?1
""")
    List<RedisPromotionResponse> findAllByPromotionStatus(PromotionStatus promotionStatus);

    @Query("""
    select new com.ohgiraffers.timedeal.core.api.controller.v1.response.PromotionResponse(
    pm.id
        ,pm.adminId
        ,pm.productId
        ,pm.salePrice
        ,pm.discountRate
        ,pm.totalQuantity
        ,pm.startTime
        ,pm.endTime
        ,pd.name
        ,pd.imageUrl
        ,pd.price)
    from Promotion pm
    join Product pd
""")
    List<PromotionResponse> findAllPromotions();

    List<Promotion> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promotion p where p.id = :id")
    public Optional<Promotion> findByIdWithPessimisticLock(Long id);
}
