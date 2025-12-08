package com.ohgiraffers.queue.core.enums;

/**
 * 큐 상태
 * 
 * WAITING: 대기열에 존재
 * PROCEED: 진행큐에 존재
 * EXPIRED: 존재하지 않음
 */
public enum QueueStatus {
    WAITING, PROCEED, EXPIRED
}
