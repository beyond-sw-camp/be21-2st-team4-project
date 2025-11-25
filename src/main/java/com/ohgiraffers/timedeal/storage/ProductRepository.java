package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Example, Long> {
}
