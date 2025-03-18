package com.onebyone.kindergarten.domain.user.exception;

public class InvalidPasswordException extends RuntimeException {
    private String message;

    public InvalidPasswordException(String message) {
        this.message = message;
    }
}
