package com.onebyone.kindergarten.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    INVALID_PASSWORD_ERROR("E0001","비밀번호가 일치하지 않습니다."),
    NOT_FOUND_EMAIL("E0002", "이메일이 존재하지 않습니다."),
    PASSWORD_MISMATCH_ERROR("E0003", "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_INQUIRY("E0004","문의를 찾을 수 없습니다."),
    INQUIRY_NOT_ADMIN_CANNOT_WRITE("E0005","관리자가 아니면 문의를 작성할 수 없습니다."),
    INQUIRY_NOT_ADMIN_CANNOT_READ("E0006","관리자가 아니면 문의를 읽을 수 없습니다."),
    POST_NOT_FOUND("E0007", "게시글을 찾을 수 없습니다."),
    KINDERGARTEN_NOT_FOUND("E0008","유치원을 찾을 수 없습니다."),
    WORK_HISTORY_NOT_FOUND("E0009","이력을 찾을 수 없습니다."),
    UNAUTHORIZED_DELETE("E0010","삭제 권한이 없습니다."),
    NOT_FOUND_REPORT("E0011","신고를 찾을 수 없습니다."),
    INVALID_REPORT_STATUS("E0012", "잘못된 신고 상태입니다."),
    INVALID_REPORT_POST_TARGET("E0013", "존재하지 않는 신고 게시글입니다."),
    NOT_FOUND_NOTICE("E0014", "존재하지 않는 공지사항입니다."),
    ALREADY_EXIST_INTERNSHIP_REVIEW("E0015", "이미 등록된 실습 리뷰가 존재합니다."),
    NOT_FOUND_INTERNSHIP_REVIEW("E0016", "실습 리뷰가 존재하지 않습니다."),
    ALREADY_EXIST_WORK_REVIEW("E0017","이미 등록된 근무 리뷰가 존재합니다."),
    NOT_FOUND_WORK_REVIEW("E0018", "근무 리뷰가 존재하지 않습니다."),
    INCORRECT_USER_EXCEPTION("E0019", "유저가 일치하지 않습니다."),
    INVALID_TOKEN_EXPIRED("E0020", "만료된 토큰입니다."),
    INVALID_TOKEN_UNSUPPORTED("E0021","지원되지 않는 토큰 형식입니다."),
    INVALID_TOKEN_MALFORMED("E0022","구조가 잘못된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE("E0023","서명이 올바르지 않은 토큰입니다."),
    INVALID_TOKEN_ILLEGAL("E0024","잘못 생성된 토큰입니다."),
    NOTIFICATION_ERROR("E0025", "알림 전송 중 오류가 발생했습니다."),
    ENTITY_NOT_FOUND_EXCEPTION("E0026","유치원을 찾을 수 없습니다."),
    HTTP_MESSAGE_NOT_REDABLE_EXCEPTION("E0027","잘못된 요청 형식입니다."),
    ALREADY_EXIST_EMAIL("E0028","이미 존재하는 이메일입니다."),
    NOT_FOUND_EXCEPTION_BY_TEMPORARY_PASSWORD_EXCEPTION("E0029","이메일이 인증되지 않았습니다."),
    ILLEGAL_ARGUMENT_STAR_RATING_EXCEPTION("E0030","starRating은 1부터 5 사이의 값이어야 합니다."),
    ALREADY_BLOCK_USER("E0031","이미 차단한 사용자입니다."),
    SELF_BLOCK_NOT_ALLOWED("E0032","자기 자신을 차단할 수 없습니다."),
    NOT_FOUND_POST("E0033","게시글을 찾을 수 없습니다."),
    NOT_FOUND_PARENT_COMMENT("E0034", "원 댓글을 찾을 수 없습니다."),
    PARENT_POST_MISMATCH("E0035", "원 댓글의 게시글이 일치하지 않습니다."),
    REPLY_TO_REPLY_NOT_ALLOWED("E0036", "대댓글에는 답글을 작성할 수 없습니다."),
    REPLY_TO_DELETED_COMMENT_NOT_ALLOWED("E0037", "삭제된 댓글에는 답글을 작성할 수 없습니다."),
    NOT_FOUND_COMMENT("E0038", "댓글이 존재하지 않습니다."),
    REVIEW_EDIT_NOT_OWNER("E0039", "본인의 리뷰만 수정할 수 있습니다."),
    NOT_FOUND_USER("E0040", "사용자가 존재하지 않습니다."),
    INVALID_REPORT_COMMENT_TARGET("E0041", "존재하지 않는 신고 댓글입니다."),
    REVIEW_REPORT_NOT_IMPLEMENTED("E0042", "리뷰 처리는 아직 구현되지 않았습니다."),
    INVALID_REPORT_TARGET_TYPE("E0043", "지원하지 않는 신고 대상 타입입니다."),
    FAILED_TOP_POST_CACHE_EXCEPTION("E0044", "인기 게시글 캐시 갱신에 실패했습니다."),
    FAILED_SEND_MAIL_EXCEPTION("E0045", "메일 발송에 실패했습니다"),
    ALREADY_EXIST_EMAIL_CERTIFICATION("E0046", "기존에 발급된 인증번호가 존재합니다."),
    FAILED_EMAIL_CERTIFICATION_EXCEPTION("E0047", "인증되지 않은 이메일입니다."),
    FAILED_AUTHORIZATION_EXCEPTION("E0048", "인증되지 않은 요청입니다."),
    INCORRECT_KINDERGARTEN_EXCEPTION("E0049", "유치원이 일치하지 않습니다."),
    INVALID_REPORT_INTERNSHIP_REVIEW_TARGET("E0050", "존재하지 않는 실습 신고 리뷰입니다."),
    INVALID_REPORT_WORK_REVIEW_TARGET("E0051", "존재하지 않는 근무 신고 리뷰입니다."),
    BATCH_NOT_ADMIN_CANNOT_USE("E0052", "관리자가 아니면 배치를 사용할 수 없습니다."),
    INTERNAL_SERVER_ERROR("E9999","알 수 없는 에러 발생");

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
