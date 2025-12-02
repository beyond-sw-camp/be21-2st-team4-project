package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 특정 Admin 소유 상품 조회
    List<Product> findByAdminId(Long adminId);
}