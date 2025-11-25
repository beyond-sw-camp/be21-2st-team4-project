package com.ohgiraffers.timedeal.storage;

import com.ohgiraffers.timedeal.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
