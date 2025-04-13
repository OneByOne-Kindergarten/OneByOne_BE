package com.onebyone.kindergarten.global.exception;

public class ExpiredTokenException extends RuntimeException  {
    private String value;

    public ExpiredTokenException(String value) {
        super(value);
        this.value = value;
    }
}
