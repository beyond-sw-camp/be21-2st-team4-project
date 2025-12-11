package com.ohgiraffers.order.core.api.command.queue;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.client.QueueClient;
import com.ohgiraffers.order.core.api.command.common.ApiResultHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueValidator {

    private final ApiResultHandler apiResult;
    private final QueueClient client;

    public void verify(Long timedealId, Long userId) {
        apiResult.unwrap(
                client.verifyQueue(timedealId, userId),
                () -> new CoreException(ErrorType.QUEUE_NOT_PASSED)
        );
    }

    public boolean complete(Long timedealId, Long userId) {

        boolean result = apiResult.unwrap(
                client.completeQueue(timedealId, userId),
                () -> new CoreException(ErrorType.FEIGN_CLIENT_ERROR)
        );

        if (!result) {
            throw new CoreException(ErrorType.QUEUE_NOT_PASSED);
        }
        return result;
    }
}
