package com.onebyone.kindergarten.global.exception;

public class BusinessException extends RuntimeException {
  private final ErrorCodes errorCodes;

  public BusinessException(ErrorCodes errorCodes) {
    super(errorCodes.getCode());
    this.errorCodes = errorCodes;
  }

  public ErrorCodes getErrorCode() {
    return errorCodes;
  }
}
