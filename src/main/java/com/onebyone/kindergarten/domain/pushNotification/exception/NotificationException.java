package com.onebyone.kindergarten.domain.pushNotification.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {
    private String message;

    public NotificationException(String message) {
        super(message);
        this.message = message;
    }
} 