package com.onebyone.kindergarten.domain.inquires.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InquiryNotAdminReadException extends RuntimeException {
    public InquiryNotAdminReadException(String message) {
        super(message);
    }
}
