package com.ohgiraffers.account.storage;


import com.ohgiraffers.account.core.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsById(Long id);
}