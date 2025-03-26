package com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception;

import lombok.Getter;

@Getter
public class UnauthorizedDeleteException extends RuntimeException {
    public UnauthorizedDeleteException() {
        super("삭제 권한이 없습니다.");
    }
} 