package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.AdminsRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminsRepository adminsRepository;
    public AdminService(AdminsRepository adminsRepository) {
        this.adminsRepository = adminsRepository;
    }
}
