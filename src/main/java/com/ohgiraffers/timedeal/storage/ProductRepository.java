package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.admin.id = :adminId")
    List<Product> findByAdminIdWithCategory(Long adminId);

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :productId")
    Optional<Product> findByIdWithCategory(Long productId);
}