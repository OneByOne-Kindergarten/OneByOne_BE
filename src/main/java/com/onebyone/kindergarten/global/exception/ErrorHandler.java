package com.onebyone.kindergarten.global.exception;

import com.onebyone.kindergarten.domain.communityPosts.exception.PostNotFoundException;
import com.onebyone.kindergarten.domain.inquires.exception.InquiryNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.KindergartenNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.UnauthorizedDeleteException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.WorkHistoryNotFoundException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportStatusException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportTargetException;
import com.onebyone.kindergarten.domain.reports.exception.ReportNotFoundException;
import com.onebyone.kindergarten.domain.sample.exception.SampleException;
import com.onebyone.kindergarten.domain.user.exception.InvalidPasswordException;
import com.onebyone.kindergarten.domain.user.exception.NotFoundEmailException;
import com.onebyone.kindergarten.domain.user.exception.PasswordMismatchException;
import com.onebyone.kindergarten.global.error.Error;
import com.onebyone.kindergarten.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 기본적으로 500 처리
    public ErrorResponse handleAllExceptions(Exception e) {
        log.error("알 수 없는 예외 발생: {}", e.getMessage(), e);
        return buildError(Error.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InquiryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse inQueryNotFoundExceptions (InquiryNotFoundException error) {
        log.error("InquiryNotFoundException 발생: {}", error.getMessage(), error);
        return buildError(Error.INQUIRY_NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse postNotFoundExceptions (PostNotFoundException error) {
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

    private ErrorResponse buildError(Error error) {
        ErrorResponse retError = ErrorResponse.builder()
                .message(error.getMessage())
                .build();
        return retError;
    }
}