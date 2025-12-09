package com.ohgiraffers.common.support.response;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgiraffers.common.support.error.ErrorMessage;
import com.ohgiraffers.common.support.error.ErrorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ApiResult<S> {
    private final ResultType result;
    private final S data;
    private final ErrorMessage error;

    @JsonCreator
    public ApiResult(
            @JsonProperty("result") ResultType result,
            @JsonProperty("data") S data,
            @JsonProperty("error") ErrorMessage error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public static ApiResult<?> success() {
        return new ApiResult<>(ResultType.SUCCESS, null, null);
    }

    public static <S> ApiResult<S> success(S data) {
        return new ApiResult<>(ResultType.SUCCESS, data, null);
    }

    public static ApiResult<?> error(ErrorType error) {
        return new ApiResult<>(ResultType.ERROR, null, new ErrorMessage(error));
    }

    public static ApiResult<?> error(ErrorType error, Object errorData) {
        return new ApiResult<>(ResultType.ERROR, null, new ErrorMessage(error, errorData));
    }
}
