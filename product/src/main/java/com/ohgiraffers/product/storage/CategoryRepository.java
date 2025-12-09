package com.ohgiraffers.product.storage;

import com.ohgiraffers.product.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}