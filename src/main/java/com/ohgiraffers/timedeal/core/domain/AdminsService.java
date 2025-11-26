package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.AdminsRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminsService {
    private final AdminsRepository adminsRepository;
    public AdminsService(AdminsRepository adminsRepository) {
        this.adminsRepository = adminsRepository;
    }
}
