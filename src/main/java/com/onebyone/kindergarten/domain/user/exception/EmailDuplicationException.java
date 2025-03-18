package com.onebyone.kindergarten.domain.user.exception;

public class EmailDuplicationException extends RuntimeException{
    private String message;

    public EmailDuplicationException(String message) {}
}
