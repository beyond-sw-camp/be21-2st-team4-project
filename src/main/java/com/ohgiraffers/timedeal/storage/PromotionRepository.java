package com.ohgiraffers.timedeal.storage;


import com.ohgiraffers.timedeal.core.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {
}
