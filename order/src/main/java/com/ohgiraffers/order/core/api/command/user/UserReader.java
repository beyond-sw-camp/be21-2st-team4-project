package com.ohgiraffers.order.core.api.command.user;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.order.core.api.command.common.ApiResultHandler;
import com.ohgiraffers.order.core.api.command.client.UserClient;
import com.ohgiraffers.order.core.api.command.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserClient client;
    private final ApiResultHandler apiResult;

    public UserResponse getUser(Long id) {

        return apiResult.unwrap(
                client.getUser(id),
                () -> new CoreException(ErrorType.USER_READ_FAILED)
        );
    }

    public void decreaseMoney(Long id, Integer price) {

        apiResult.unwrap(
            client.decreaseMoney(id, price),
            () -> new CoreException(ErrorType.USER_BALANCE_DECREASE_FAILED)
        );
    }
}
