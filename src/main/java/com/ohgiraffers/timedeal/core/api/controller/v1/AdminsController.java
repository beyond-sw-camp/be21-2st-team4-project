package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.domain.AdminsService;
import com.ohgiraffers.timedeal.core.domain.ProductService;
import com.ohgiraffers.timedeal.storage.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminsController {

    private final AdminsService adminsService;

    @Autowired
    public AdminsController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }
}
