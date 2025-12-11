package com.ohgiraffers.order.core.api.command.queue;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.order.core.api.command.client.QueueClient;
import com.ohgiraffers.order.core.api.command.common.ApiResultHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueReader {

    private final ApiResultHandler apiResult;
    private final QueueClient client;

    public void verify(Long timedealId, Long userId) {
        apiResult.unwrap(
                client.verifyQueue(timedealId, userId),
                () -> new CoreException(ErrorType.DEFAULT_ERROR)
        );
    }

    public boolean complete(Long timedealId, Long userId) {
        return apiResult.unwrap(
                client.completeQueue(timedealId, userId),
                () -> new CoreException(ErrorType.DEFAULT_ERROR)
        );
    }
}
