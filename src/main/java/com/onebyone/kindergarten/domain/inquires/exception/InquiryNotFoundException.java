package com.onebyone.kindergarten.domain.inquires.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InquiryNotFoundException extends RuntimeException {
    public InquiryNotFoundException(String message) {
        super(message);
    }
} 