package com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception;

import lombok.Getter;

@Getter
public class AlreadyExistInternshipReviewException extends RuntimeException {
    private String value;

    public AlreadyExistInternshipReviewException(String value) {
        this.value = value;
    }
}
