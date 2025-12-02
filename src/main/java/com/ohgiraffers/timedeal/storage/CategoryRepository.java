package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}