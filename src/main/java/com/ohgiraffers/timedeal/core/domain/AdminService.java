package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.storage.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
}
