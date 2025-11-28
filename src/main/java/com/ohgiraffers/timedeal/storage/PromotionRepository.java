package com.ohgiraffers.timedeal.storage;


import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {



    Optional<Promotion>findByProductId(Long productId);

    Promotion findByPromotionStatus(Long productId);

    Promotion save(Promotion promotion);

    void promotionUpdateStatusById(Long id);
}
