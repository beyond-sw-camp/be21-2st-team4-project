package com.ohgiraffers.common.support.error;

public enum ErrorCode {

    E500,
    E400,

    // REDIS ERROR
    REDIS_ERROR,
    REDIS_CONN_FAILURE,

    // USER
    USER_NOT_FOUND,

    // QUEUE ERROR
    QUEUE_NOT_FOUND,
    QUEUE_EXPIRED,
}
