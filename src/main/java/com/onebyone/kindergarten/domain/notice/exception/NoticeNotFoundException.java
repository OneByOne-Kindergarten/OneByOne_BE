package com.onebyone.kindergarten.domain.notice.exception;

public class NoticeNotFoundException extends RuntimeException {
    public NoticeNotFoundException() {
        super("존재하지 않는 공지사항입니다.");
    }
} 