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
    REDIS_LOCK_EXPIRED(HttpStatus.GONE, ErrorCode.REDIS_LOCK_EXPIRED, "락 유효 시간이 만료되었습니다.", LogLevel.WARN),

    // QUEUE ERROR,
    QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.QUEUE_NOT_FOUND, "대기열이 존재하지 않습니다.", LogLevel.WARN),
    QUEUE_EXPIRED(HttpStatus.GONE, ErrorCode.QUEUE_EXPIRED, "진행큐에 기간이 만료되었습니다.", LogLevel.WARN),
    QUEUE_NOT_PASSED(HttpStatus.FORBIDDEN, ErrorCode.QUEUE_NOT_PASSED, "대기열을 통과하지 못했습니다.", LogLevel.WARN),
    QUEUE_NOT_COMPLETED(HttpStatus.BAD_REQUEST, ErrorCode.QUEUE_NOT_COMPLETED, "대기열이 완료되지 않았습니다.", LogLevel.WARN),

    // PRODUCT ERROR
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND, "상품이 존재하지 않습니다.", LogLevel.WARN),
    PRODUCT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_CATEGORY_NOT_FOUND, "상품 카테고리가 존재하지 않습니다.", LogLevel.WARN),
    PRODUCT_UNAUTHORIZED(HttpStatus.FORBIDDEN, ErrorCode.PRODUCT_UNAUTHORIZED, "상품에 대한 권한이 없습니다.", LogLevel.WARN),
    PRODUCT_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.PRODUCT_VALIDATION_ERROR, "상품 요청 값이 올바르지 않습니다.", LogLevel.WARN),

    // PROMOTION ERROR
    PROMOTION_SOLDQUANTITY_OVER(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_SOLDQUANTITY_OVER, "프로모션 판매 수량을 초과했습니다.", LogLevel.WARN),
    PROMOTION_STATUS_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_STATUS_ERROR, "프로모션 상태가 올바르지 않습니다.", LogLevel.WARN),
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.PROMOTION_NOT_FOUND, "프로모션이 존재하지 않습니다.", LogLevel.WARN),
    PROMOTION_STATUS_INVALID(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_STATUS_INVALID, "프로모션이 활성화된 상태가 아닙니다.", LogLevel.WARN),


    // ORDER REQUEST VALIDATION ERROR
    PROMOTION_ID_INVALID(HttpStatus.BAD_REQUEST, ErrorCode.PROMOTION_ID_INVALID, "프로모션 ID가 유효하지 않습니다.", LogLevel.WARN),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, ErrorCode.OUT_OF_STOCK, "재고가 부족합니다.", LogLevel.WARN),
    QUANTITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, ErrorCode.QUANTITY_LIMIT_EXCEEDED, "구매 가능한 수량을 초과했습니다.", LogLevel.WARN),

    // ORDER(USER ERROR)
    USER_READ_FAILED(HttpStatus.NOT_FOUND, ErrorCode.USER_READ_FAILED, "유저 조회에 실패했습니다.", LogLevel.WARN),
    USER_BALANCE_DECREASE_FAILED(HttpStatus.BAD_REQUEST, ErrorCode.USER_BALANCE_DECREASE_FAILED, "잔액 차감에 실패했습니다.", LogLevel.WARN),

    // ORDER(PROMOTION ERROR)
    PROMOTION_READ_FAILED(HttpStatus.NOT_FOUND, ErrorCode.PROMOTION_READ_FAILED, "프로모션 조회에 실패했습니다.", LogLevel.WARN),
    PROMOTION_STOCK_DECREASE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.PROMOTION_STOCK_DECREASE_FAILED, "재고 차감에 실패했습니다.", LogLevel.ERROR),

    // ORDER DB ERROR
    ORDER_DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ORDER_DB_ERROR, "주문 처리 중 DB 오류가 발생했습니다.", LogLevel.ERROR),

    // FEIGN ERROR
    FEIGN_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, ErrorCode.FEIGN_CLIENT_ERROR, "외부 서비스 호출 중 오류가 발생했습니다.", LogLevel.ERROR),


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
