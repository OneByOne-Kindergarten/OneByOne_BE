package com.onebyone.kindergarten.global.exception;

public class UnsupportedTokenException extends RuntimeException {
    private String value;

    public UnsupportedTokenException(String value) {
        this.value = value;
    }
}