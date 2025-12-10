package com.ohgiraffers.account.core.api.controller.v1.response;

public record AdminSignInResponse (
        Long adminId,
        String token
){
}
