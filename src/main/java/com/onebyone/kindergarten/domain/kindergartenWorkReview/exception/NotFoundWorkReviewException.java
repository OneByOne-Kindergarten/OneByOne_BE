package com.onebyone.kindergarten.domain.kindergartenWorkReview.exception;

public class NotFoundWorkReviewException extends RuntimeException {
    private String value;

    public NotFoundWorkReviewException(String value) {
        this.value = value;
    }
}
