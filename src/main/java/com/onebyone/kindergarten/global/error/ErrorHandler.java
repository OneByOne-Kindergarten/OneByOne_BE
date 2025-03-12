package com.onebyone.kindergarten.global.error;

import com.onebyone.kindergarten.domain.sample.exception.SampleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(SampleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleSampleException(SampleException e) {
        log.error("SampleException 발생: {}", e.getMessage(), e);
        return buildError(Error.SAMPLE);
    }

    private ErrorResponse buildError(Error error) {
        ErrorResponse retError = ErrorResponse.builder()
                .message(error.getMessage())
                .build();
        return retError;
    }
}