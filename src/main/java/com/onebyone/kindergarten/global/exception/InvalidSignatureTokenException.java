package com.onebyone.kindergarten.global.exception;

public class InvalidSignatureTokenException extends RuntimeException  {
    private String value;

    public InvalidSignatureTokenException(String value) {
        this.value = value;
    }
}
