package com.ohgiraffers.product.storage;

import com.ohgiraffers.product.core.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
