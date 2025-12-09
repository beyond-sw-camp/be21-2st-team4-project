package com.ohgiraffers.common.support.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ErrorMessage {
    private final String code;
    private final String message;
    private final Object data;

    @JsonCreator
    public ErrorMessage(
            @JsonProperty("code") String code,
            @JsonProperty("message") String message,
            @JsonProperty("data") Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ErrorMessage(ErrorType errorType) {
        this.code = errorType.getCode().name();
        this.message = errorType.getMessage();
        this.data = null;
    }

    public ErrorMessage(ErrorType errorType, Object data) {
        this.code = errorType.getCode().name();
        this.message = errorType.getMessage();
        this.data = data;
    }
}
