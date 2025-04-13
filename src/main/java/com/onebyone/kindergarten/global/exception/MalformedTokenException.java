package com.onebyone.kindergarten.global.exception;

public class MalformedTokenException extends RuntimeException {
    private String value;

    public MalformedTokenException(String value) {
        this.value = value;
    }
}
