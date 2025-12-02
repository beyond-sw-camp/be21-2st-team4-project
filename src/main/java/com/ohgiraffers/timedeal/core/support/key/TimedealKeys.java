package com.ohgiraffers.timedeal.core.support.key;

/**
 *  Keys 규칙
 *  Redis에서는 콜론(:)으로 계층을 구분하는 것이 표준 관례 (서비스:도메인:식별자:용도)
 *
 *  예제
 *  timedeal:{timedealId}:wait-queue      # 대기열
 *  timedeal:{timedealId}:proceed-queue   # 처리 진행 중
 *  timedeal:{timedealId}:stock           # 잔여 재고
 *  timedeal:{timedealId}:completed       # 구매 완료 사용자
 */

public class TimedealKeys {
    private static final String PREFIX = "timedeal";

    public static String waitQueue(Long timedealId) {
        return PREFIX + ":" + timedealId + ":wait-queue";
    }

    public static String proceedQueue(Long timedealId) {
        return PREFIX + ":" + timedealId + ":stock";
    }

    public static String setPromotion(Long timedealId) {return PREFIX + ":" + timedealId + ":set-promotion";}

    public static String deletePromotion(Long timedealId) {
        return PREFIX + ":" + timedealId + ":stock";
    }
}
