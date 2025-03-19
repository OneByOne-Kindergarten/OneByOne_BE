package com.onebyone.kindergarten.domain.user.exception;

public class PasswordMismatchException extends RuntimeException {
    private String message;

    public PasswordMismatchException(String message) {
        this.message = message;
    }
}
