package com.ohgiraffers.common.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    // COMMON ERROR
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    DEFAULT_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, ErrorCode.E400, "An unexpected error has occurred.", LogLevel.WARN),

    // USER ERROR
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다.", LogLevel.WARN),
    USER_NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND_EMAIL, "일치하는 이메일이 없습니다.", LogLevel.WARN),
    USER_NOT_EQUALS_PASSWORD(HttpStatus.UNAUTHORIZED,ErrorCode.USER_NOT_EQUALS_PASSWORD, "비밀번호가 일치하지 않습니다.", LogLevel.WARN),
    USER_EXISTS_EMAIL(HttpStatus.CONFLICT, ErrorCode.USER_EXISTS_EMAIL, "동일한 이메일이 존재합니다.", LogLevel.WARN),


    // REDIS ERROR
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.REDIS_ERROR, "레디스 알 수 없는 예외 발생", LogLevel.ERROR),
    REDIS_CONN_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.REDIS_CONN_FAILURE, "레디스 연결 또는 타임아웃 발생", LogLevel.ERROR),

    // QUEUE ERROR,
    QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.QUEUE_NOT_FOUND, "대기열이 존재하지 않습니다.", LogLevel.WARN),
    QUEUE_EXPIRED(HttpStatus.GONE, ErrorCode.QUEUE_EXPIRED, "진행큐에 기간이 만료되었습니다.", LogLevel.WARN),

    // PRODUCT ERROR
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND, "상품이 존재하지 않습니다.", LogLevel.WARN),
    PRODUCT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_CATEGORY_NOT_FOUND, "상품 카테고리가 존재하지 않습니다.", LogLevel.WARN),
    PRODUCT_UNAUTHORIZED(HttpStatus.FORBIDDEN, ErrorCode.PRODUCT_UNAUTHORIZED, "상품에 대한 권한이 없습니다.", LogLevel.WARN),
    PRODUCT_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.PRODUCT_VALIDATION_ERROR, "상품 요청 값이 올바르지 않습니다.", LogLevel.WARN),

    // PROMOTION ERROR
    PROMOTION_SOLDQUANTITY_OVER(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_SOLDQUANTITY_OVER, "프로모션 판매 수량을 초과했습니다.", LogLevel.WARN),
    PROMOTION_STATUS_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_STATUS_ERROR, "프로모션 상태가 올바르지 않습니다.", LogLevel.WARN),
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PROMOTION_NOT_FOUND, "프로모션이 존재하지 않습니다.", LogLevel.WARN),

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
