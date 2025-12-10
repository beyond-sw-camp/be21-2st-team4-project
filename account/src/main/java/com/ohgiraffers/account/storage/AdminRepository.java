package com.ohgiraffers.account.storage;


import com.ohgiraffers.account.core.domain.Admin;
import com.ohgiraffers.account.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    Optional<Admin> findByEmail(String email);
}