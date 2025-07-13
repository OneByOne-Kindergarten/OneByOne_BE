package com.onebyone.kindergarten.global.exception;

public class IllegalArgumentStarRatingException extends RuntimeException {
    private String value;

    public IllegalArgumentStarRatingException(String value) {
        super(value);
        this.value = value;
    }
}
