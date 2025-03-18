package com.onebyone.kindergarten.domain.user.exception;

public class NotFoundEmailException  extends RuntimeException{
    private String message;

    public NotFoundEmailException(String message) {}
}
