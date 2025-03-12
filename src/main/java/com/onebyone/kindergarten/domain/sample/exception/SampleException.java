package com.onebyone.kindergarten.domain.sample.exception;

import lombok.Getter;

@Getter
public class SampleException extends RuntimeException {
    private String value;

    public SampleException(String value) {
        this.value = value;
    }
}
