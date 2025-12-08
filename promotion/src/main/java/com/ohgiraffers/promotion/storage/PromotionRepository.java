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

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {



    Optional<Promotion>findByProductId(Long productId);

    Promotion findPromotionById(Long id);

    List<RedisPromotionResponse> findAllByPromotionStatus(PromotionStatus promotionStatus);

    List<PromotionResponse> findPromotionByPromotionStatus(PromotionStatus promotionStatus);

    PromotionStatus findPromotionStatusById(Long id);



    @Query("""
    select new com.ohgiraffers.promotion.core.api.controller.v1.response.PromotionResponse(
         pm.id
        ,pm.adminId
        ,pm.productId
        ,pm.salePrice
        ,pm.discountRate
        ,pm.totalQuantity
        ,pm.startTime
        ,pm.endTime)
    from Promotion pm
""")
    List<PromotionResponse> findAllPromotions();

    List<Promotion> findAll();

    @Modifying
    @Query("""
    update Promotion 
    set promotionStatus = :status
    where id = :id
""")
    void updatePromotionStatus(Long id,PromotionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promotion p where p.id = :id")
    public Optional<Promotion> findByIdWithPessimisticLock(Long id);

    void findSoldQuantityById(Long id, Long soldQuantity);
    @Modifying
    @Query("""
    update Promotion
    set soldQuantity = :soldQuantity
    where id = :id
""")
    void updatePromotionSoldQuantity(Long id, Long soldQuantity);
    Long findSoldQuantityById(Long id);

    OrderResponse findOrderResponseById(long id);
}
