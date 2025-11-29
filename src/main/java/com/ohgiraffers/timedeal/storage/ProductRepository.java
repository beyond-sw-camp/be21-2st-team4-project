package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Example;
import com.ohgiraffers.timedeal.core.domain.product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Example, Long> {
}
