package com.ohgiraffers.timedeal.core.api.controller.v1.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignUpRequest {
    private final String email;
    private final String password;
    private final String name;

}
