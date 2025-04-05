package com.onebyone.kindergarten.domain.kindergartenWorkReview.exception;

public class AlreadyExistWorkReviewException extends RuntimeException {
    private String value;

    public AlreadyExistWorkReviewException(String value) {
        this.value = value;
    }
}
