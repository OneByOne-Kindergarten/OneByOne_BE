package com.onebyone.kindergarten.domain.inquires.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InquiryNotAdminWriteException extends RuntimeException {
    public InquiryNotAdminWriteException(String message) {
        super(message);
    }
}
