package com.onebyone.kindergarten.global.exception;

public class InvalidTokenArgumentException extends RuntimeException {
    private String value;

    public InvalidTokenArgumentException(String value) {
        this.value = value;
    }
}
