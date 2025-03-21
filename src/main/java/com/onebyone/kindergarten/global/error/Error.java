package com.onebyone.kindergarten.global.error;

import lombok.Getter;

@Getter
public enum Error {
//    INVALID_INPUT_VALUE("입력값이 올바르지 않습니다."),
//    EMAIL_DUPLICATION("중복된 이메일입니다."),
//    ACCOUNT_NOT_FOUND("계정을 찾을 수 없습니다."),
//    INVALID_TOKEN("올바르지 않은 Token 입니다."),
//    REFRESH_TOKEN_NOT_FOUND("이미 로그아웃된 사용자입니다."),
//    POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
//    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.");
    INVALID_PASSWORD_ERROR("비밀번호가 일치하지 않습니다."),
    SAMPLE("예시 에러입니다"),
    NOT_FOUND_EMAIL_ERROR("이메일이 존재하지 않습니다."),
    PASSWORD_MISMATCH_ERROR("비밀번호가 일치하지 않습니다.");
    private final String message;

    Error(String message) {
        this.message = message;
    }
}
