package com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception;

import lombok.Getter;

@Getter
public class KindergartenNotFoundException extends RuntimeException {
    public KindergartenNotFoundException() {
        super("유치원을 찾을 수 없습니다.");
    }
} 