package com.onebyone.kindergarten.global.error;

import lombok.Getter;

@Getter
public enum Error {
    // INVALID_INPUT_VALUE("입력값이 올바르지 않습니다."),
    // EMAIL_DUPLICATION("중복된 이메일입니다."),
    // ACCOUNT_NOT_FOUND("계정을 찾을 수 없습니다."),
    // INVALID_TOKEN("올바르지 않은 Token 입니다."),
    // REFRESH_TOKEN_NOT_FOUND("이미 로그아웃된 사용자입니다."),
    // POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
    // COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.");
    INVALID_PASSWORD_ERROR("비밀번호가 일치하지 않습니다."),
    SAMPLE("예시 에러입니다"),
    NOT_FOUND_EMAIL_ERROR("이메일이 존재하지 않습니다."),
    PASSWORD_MISMATCH_ERROR("비밀번호가 일치하지 않습니다."),
    INTERNAL_SERVER_ERROR("알 수 없는 에러 발생"),
    INQUIRY_NOT_FOUND("문의를 찾을 수 없습니다."),
    INQUIRY_NOT_ADMIN_CANNOT_WRITE("관리자가 아니면 문의를 작성할 수 없습니다."),
    INQUIRY_NOT_ADMIN_CANNOT_READ("관리자가 아니면 문의를 읽을 수 없습니다."),
    POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
    KINDERGARTEN_NOT_FOUND("유치원을 찾을 수 없습니다."),
    WORK_HISTORY_NOT_FOUND("이력을 찾을 수 없습니다."),
    UNAUTHORIZED_DELETE("삭제 권한이 없습니다."),
    REPORT_NOT_FOUND("신고를 찾을 수 없습니다."),
    INVALID_REPORT_STATUS("잘못된 신고 상태입니다."),
    INVALID_REPORT_TARGET("존재하지 않는 신고 대상입니다."),
    NOTICE_NOT_FOUND("존재하지 않는 공지사항입니다."),
    ALREADY_EXIST_INTERNSHIP_REVIEW("이미 등록된 실습 리뷰가 존재합니다."),
    NOT_FOUND_INTERNSHIP_REVIEW("실습 리뷰가 존재하지 않습니다."),
    ALREADY_EXIST_WORK_REVIEW("이미 등록된 근무 리뷰가 존재합니다."),
    NOT_FOUND_WORK_REVIEW("근무 리뷰가 존재하지 않습니다."),
    INCORRECT_USER_EXCEPTION("유저가 일치하지 않습니다."),
    INVALID_TOKEN_EXPIRED("만료된 토큰입니다."),
    INVALID_TOKEN_UNSUPPORTED("지원되지 않는 토큰 형식입니다."),
    INVALID_TOKEN_MALFORMED("구조가 잘못된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE("서명이 올바르지 않은 토큰입니다."),
    INVALID_TOKEN_ILLEGAL("잘못 생성된 토큰입니다."),
    NOTIFICATION_ERROR("알림 전송 중 오류가 발생했습니다."),
    ENTITY_NOT_FOUND_EXCEPTION("유치원을 찾을 수 없습니다."),
    HTTP_MESSAGE_NOT_REDABLE_EXCEPTION("잘못된 요청 형식입니다."),
    EMAIL_DUPLICATION_EXCEPTION("이메일이 중복 되었습니다."),
    NOT_FOUND_EXCEPTION_BY_TEMPORARY_PASSWORD_EXCEPTION("이메일이 인증되지 않았습니다."),
    ILLEGAL_ARGUMENT_STAR_RATING_EXCEPTION("starRating은 1부터 5 사이의 값이어야 합니다."),
    ALREADY_BLOCK_USER("이미 차단한 사용자입니다."),
    SELF_BLOCK_NOT_ALLOWED("자기 자신을 차단할 수 없습니다.");

    private final String message;

    Error(String message) {
        this.message = message;
    }
}
