package com.ohgiraffers.timedeal.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminRequest {
    private String email;

    @NotBlank
    private String password;

    private String company;
}