package com.onebyone.kindergarten.global.jwt.exception;

public class InvalidTokenException extends RuntimeException{
    private String message;

    public InvalidTokenException(String message) {
        this.message = message;
    }
}
