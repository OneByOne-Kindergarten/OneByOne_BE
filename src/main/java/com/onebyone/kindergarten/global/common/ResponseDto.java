package com.onebyone.kindergarten.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private final Boolean success;
    private final T data;
    private final String message;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null);
    }

    public static <T> ResponseDto<T> success(T data, String message) {
        return new ResponseDto<>(true, data, message);
    }

    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>(false, null, message);
    }

    public static <T> ResponseDto<T> error(T data, String message) {
        return new ResponseDto<>(false, data, message);
    }
} 