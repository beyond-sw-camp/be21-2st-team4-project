package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.domain.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
}
