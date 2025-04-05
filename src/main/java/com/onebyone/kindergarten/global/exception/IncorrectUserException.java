package com.onebyone.kindergarten.global.exception;

public class IncorrectUserException extends RuntimeException {
    private String value;

    public IncorrectUserException(String value) {
        this.value = value;
    }
}
