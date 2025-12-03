package com.ohgiraffers.timedeal.core.api.controller.v1.response;

import com.ohgiraffers.timedeal.core.domain.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminResponse {
    private Long id;
    private String email;
    private String company;

    public static AdminResponse from(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getCompany()
        );
    }
}