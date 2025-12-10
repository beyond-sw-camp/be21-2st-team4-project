package com.ohgiraffers.product.core.api.command;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.common.support.response.ResultType;
import com.ohgiraffers.product.core.api.controller.v1.response.AdminResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandClient {
    private final AccountClient accountClient;

    public String getAdminCompany(Long adminId) {
        ApiResult<AdminResponse> response = accountClient.findAdminById(adminId);
        if(response.getResult() == ResultType.ERROR) {
            return null;
        }
        return response.getData().company();
    }
}
