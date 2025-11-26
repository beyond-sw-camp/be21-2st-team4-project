package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminsRepository extends JpaRepository<Admin, Long> {
}
