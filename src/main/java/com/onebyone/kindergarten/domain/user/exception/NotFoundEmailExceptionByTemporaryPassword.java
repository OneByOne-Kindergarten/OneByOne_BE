package com.onebyone.kindergarten.domain.user.exception;

public class NotFoundEmailExceptionByTemporaryPassword extends RuntimeException{
    private String message;

    public NotFoundEmailExceptionByTemporaryPassword(String message) {}
}
