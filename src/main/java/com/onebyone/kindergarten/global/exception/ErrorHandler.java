package com.onebyone.kindergarten.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ErrorHandler {
  @ExceptionHandler(BusinessException.class)
  @ResponseBody
  public ErrorResponse handleBusinessException(BusinessException e) {
    ErrorCodes errorCodes = e.getErrorCode();
    return ErrorResponse.buildError(errorCodes);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorResponse handleAllExceptions(Exception e) {
    System.out.println(e.toString());
    return ErrorResponse.buildError(ErrorCodes.INTERNAL_SERVER_ERROR);
  }
}
