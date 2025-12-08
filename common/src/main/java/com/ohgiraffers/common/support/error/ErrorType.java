package com.ohgiraffers.common.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    // COMMON ERROR
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    DEFAULT_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, ErrorCode.E400, "An unexpected error has occurred.", LogLevel.WARN),

    // USER ERROR
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다.", LogLevel.WARN),


    // REDIS ERROR
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.REDIS_ERROR, "레디스 알 수 없는 예외 발생", LogLevel.ERROR),
    REDIS_CONN_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.REDIS_CONN_FAILURE, "레디스 연결 또는 타임아웃 발생", LogLevel.ERROR),

    // QUEUE ERROR,
    QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.QUEUE_NOT_FOUND, "대기열이 존재하지 않습니다.", LogLevel.WARN),
    QUEUE_EXPIRED(HttpStatus.GONE, ErrorCode.QUEUE_EXPIRED, "진행큐에 기간이 만료되었습니다.", LogLevel.WARN),

    MAX_ERROR(null, null, null, null);

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {

        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

}
