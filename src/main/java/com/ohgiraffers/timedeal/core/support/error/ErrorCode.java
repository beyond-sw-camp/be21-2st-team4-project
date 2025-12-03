package com.ohgiraffers.timedeal.core.support.error;

public enum ErrorCode {

    E500,
    E400,

    // REDIS ERROR
    REDIS_ERROR,

    // QUEUE ERROR
    QUEUE_NOT_FOUND,
    QUEUE_EXPIRED,
}
