package com.ohgiraffers.timedeal.core.api.controller.v1.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private String name;

}
