package com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception;

import lombok.Getter;

@Getter
public class WorkHistoryNotFoundException extends RuntimeException {
    public WorkHistoryNotFoundException() {
        super("이력을 찾을 수 없습니다.");
    }
} 