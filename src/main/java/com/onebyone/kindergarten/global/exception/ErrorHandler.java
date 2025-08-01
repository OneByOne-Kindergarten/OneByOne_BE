package com.onebyone.kindergarten.global.exception;

import com.onebyone.kindergarten.domain.communityPosts.exception.PostNotFoundException;
import com.onebyone.kindergarten.domain.inquires.exception.InquiryNotAdminReadException;
import com.onebyone.kindergarten.domain.inquires.exception.InquiryNotAdminWriteException;
import com.onebyone.kindergarten.domain.inquires.exception.InquiryNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception.AlreadyExistInternshipReviewException;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception.NotFoundInternshipReviewException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.KindergartenNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.UnauthorizedDeleteException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.WorkHistoryNotFoundException;
import com.onebyone.kindergarten.domain.notice.exception.NoticeNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.exception.AlreadyExistWorkReviewException;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.exception.NotFoundWorkReviewException;
import com.onebyone.kindergarten.domain.pushNotification.exception.NotificationException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportStatusException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportTargetException;
import com.onebyone.kindergarten.domain.reports.exception.ReportNotFoundException;
import com.onebyone.kindergarten.domain.sample.exception.SampleException;
import com.onebyone.kindergarten.domain.user.exception.EmailDuplicationException;
import com.onebyone.kindergarten.domain.user.exception.InvalidPasswordException;
import com.onebyone.kindergarten.domain.user.exception.NotFoundEmailException;
import com.onebyone.kindergarten.domain.user.exception.NotFoundEmailExceptionByTemporaryPassword;
import com.onebyone.kindergarten.domain.userBlock.exception.AlreadyBlockException;
import com.onebyone.kindergarten.domain.userBlock.exception.SelfBlockException;
import com.onebyone.kindergarten.domain.user.exception.PasswordMismatchException;
import com.onebyone.kindergarten.global.error.Error;
import com.onebyone.kindergarten.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(SampleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleSampleException(SampleException e) {
        log.error("SampleException 발생: {}", e.getMessage(), e);
        return buildError(Error.SAMPLE);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidPasswordException(InvalidPasswordException e) {
        log.error("InvalidPasswordException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_PASSWORD_ERROR);
    }

    @ExceptionHandler(NotFoundEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleNotFoundEmailException(NotFoundEmailException e) {
        log.error("NotFoundEmailException 발생: {}", e.getMessage(), e);
        return buildError(Error.NOT_FOUND_EMAIL_ERROR);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handlePasswordMismatchException(PasswordMismatchException e) {
        log.error("PasswordMismatchException 발생: {}", e.getMessage(), e);
        return buildError(Error.PASSWORD_MISMATCH_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 기본적으로 500 처리
    public ErrorResponse handleAllExceptions(Exception e) {
        log.error("알 수 없는 예외 발생: {}", e.getMessage(), e);
        return buildError(Error.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InquiryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse inQueryNotFoundExceptions(InquiryNotFoundException error) {
        log.error("InquiryNotFoundException 발생: {}", error.getMessage(), error);
        return buildError(Error.INQUIRY_NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse postNotFoundExceptions(PostNotFoundException error) {
        log.error("PostNotFoundException 발생: {}", error.getMessage(), error);
        return buildError(Error.POST_NOT_FOUND);
    }

    @ExceptionHandler(KindergartenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleKindergartenNotFoundException(KindergartenNotFoundException e) {
        log.error("KindergartenNotFoundException 발생: {}", e.getMessage(), e);
        return buildError(Error.KINDERGARTEN_NOT_FOUND);
    }

    @ExceptionHandler(WorkHistoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleWorkHistoryNotFoundException(WorkHistoryNotFoundException e) {
        log.error("WorkHistoryNotFoundException 발생: {}", e.getMessage(), e);
        return buildError(Error.WORK_HISTORY_NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedDeleteException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponse handleUnauthorizedDeleteException(UnauthorizedDeleteException e) {
        log.error("UnauthorizedDeleteException 발생: {}", e.getMessage(), e);
        return buildError(Error.UNAUTHORIZED_DELETE);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleReportNotFoundException(ReportNotFoundException e) {
        log.error("ReportNotFoundException 발생: {}", e.getMessage(), e);
        return buildError(Error.REPORT_NOT_FOUND);
    }

    @ExceptionHandler(InvalidReportStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidReportStatusException(InvalidReportStatusException e) {
        log.error("InvalidReportStatusException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_REPORT_STATUS);
    }

    @ExceptionHandler(InvalidReportTargetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidReportTargetException(InvalidReportTargetException e) {
        log.error("InvalidReportTargetException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_REPORT_TARGET);
    }

    @ExceptionHandler(InquiryNotAdminReadException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponse handleInquiryNotAdminException(InquiryNotAdminReadException e) {
        log.error("InquiryNotAdminException 발생: {}", e.getMessage(), e);
        return buildError(Error.INQUIRY_NOT_ADMIN_CANNOT_READ);
    }

    @ExceptionHandler(InquiryNotAdminWriteException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponse handleInquiryNotAdminWriteException(InquiryNotAdminWriteException e) {
        log.error("InquiryNotAdminWriteException 발생: {}", e.getMessage(), e);
        return buildError(Error.INQUIRY_NOT_ADMIN_CANNOT_WRITE);
    }

    @ExceptionHandler(NoticeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleEntityNotFoundException(NoticeNotFoundException e) {
        log.error("NoticeNotFoundException 발생: {}", e.getMessage(), e);
        return buildError(Error.NOTICE_NOT_FOUND);
    }

    @ExceptionHandler(NotFoundInternshipReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleNotFoundInternshipReviewException(NotFoundInternshipReviewException e) {
        log.error("NotFoundInternshipReviewException 발생: {}", e.getMessage(), e);
        return buildError(Error.NOT_FOUND_INTERNSHIP_REVIEW);
    }

    @ExceptionHandler(AlreadyExistInternshipReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAlreadyExistInternshipReviewException(AlreadyExistInternshipReviewException e) {
        log.error("AlearyExistsInternshipReviewException 발생: {}", e.getMessage(), e);
        return buildError(Error.ALREADY_EXIST_INTERNSHIP_REVIEW);
    }

    @ExceptionHandler(NotFoundWorkReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleNotFoundWorkReviewException(NotFoundWorkReviewException e) {
        log.error("NotFoundWorkReviewException 발생: {}", e.getMessage(), e);
        return buildError(Error.NOT_FOUND_WORK_REVIEW);
    }

    @ExceptionHandler(AlreadyExistWorkReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAlreadyExistWorkReviewException(AlreadyExistWorkReviewException e) {
        log.error("AlreadyExistWorkReviewException 발생: {}", e.getMessage(), e);
        return buildError(Error.ALREADY_EXIST_WORK_REVIEW);
    }

    @ExceptionHandler(IncorrectUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleIncorrectUserException(IncorrectUserException e) {
        log.error("IncorrectUserException 발생: {}", e.getMessage(), e);
        return buildError(Error.INCORRECT_USER_EXCEPTION);
    }

    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleExpiredTokenException(ExpiredTokenException e) {
        log.error("ExpiredTokenException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_TOKEN_EXPIRED);
    }

    @ExceptionHandler(UnsupportedTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleUnsupportedTokenException(UnsupportedTokenException e) {
        log.error("UnsupportedTokenException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_TOKEN_UNSUPPORTED);
    }

    @ExceptionHandler(MalformedTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleMalformedTokenException(MalformedTokenException e) {
        log.error("MalformedTokenException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_TOKEN_MALFORMED);
    }

    @ExceptionHandler(InvalidSignatureTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleInvalidSignatureTokenException(InvalidSignatureTokenException e) {
        log.error("InvalidSignatureTokenException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_TOKEN_SIGNATURE);
    }

    @ExceptionHandler(InvalidTokenArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleInvalidTokenArgumentException(InvalidTokenArgumentException e) {
        log.error("InvalidTokenArgumentException 발생: {}", e.getMessage(), e);
        return buildError(Error.INVALID_TOKEN_ILLEGAL);
    }

    @ExceptionHandler(NotificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleNotificationException(NotificationException e) {
        log.error("NotificationException 발생: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException 발생: {}", e.getMessage(), e);
        return buildError(Error.ENTITY_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleEnumParseError(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotRedableException 발생: {}", e.getMessage(), e);
        return buildError(Error.HTTP_MESSAGE_NOT_REDABLE_EXCEPTION);
    }

    @ExceptionHandler(EmailDuplicationException.class)
    public ErrorResponse handleEmailDuplicationError(EmailDuplicationException e) {
        log.error("EmailDuplicationException 발생: {}", e.getMessage(), e);
        return buildError(Error.EMAIL_DUPLICATION_EXCEPTION);
    }

    @ExceptionHandler(NotFoundEmailExceptionByTemporaryPassword.class)
    public ErrorResponse handleNotFoundEmailExceptionByTemporaryPasswordError(NotFoundEmailExceptionByTemporaryPassword e) {
        log.error("NotFoundEmailExceptionByTemporaryPassword 발생: {}", e.getMessage(), e);
        return buildError(Error.NOT_FOUND_EXCEPTION_BY_TEMPORARY_PASSWORD_EXCEPTION);
    }

    @ExceptionHandler(IllegalArgumentStarRatingException.class)
    public ErrorResponse handleIllegalArgumentStarRatingError(IllegalArgumentStarRatingException e) {
        log.error("IllegalArgumentStarRatingException 발생: {}", e.getMessage(), e);
        return buildError(Error.ILLEGAL_ARGUMENT_STAR_RATING_EXCEPTION);
    }

    @ExceptionHandler(AlreadyBlockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyBlockException(AlreadyBlockException e) {
        log.error("AlreadyBlockException 발생: {}", e.getMessage(), e);
        return buildError(Error.ALREADY_BLOCK_USER);
    }

    @ExceptionHandler(SelfBlockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSelfBlockException(SelfBlockException e) {
        log.error("SelfBlockException 발생: {}", e.getMessage(), e);
        return buildError(Error.SELF_BLOCK_NOT_ALLOWED);
    }

    private ErrorResponse buildError(Error error) {
        ErrorResponse retError = ErrorResponse.builder()
                .message(error.getMessage())
                .build();
        return retError;
    }
}