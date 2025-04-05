package com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception;

import lombok.Getter;

@Getter
public class NotFoundInternshipReviewException extends RuntimeException {
    private String value;

    public NotFoundInternshipReviewException(String value) {
        this.value = value;
    }
}
