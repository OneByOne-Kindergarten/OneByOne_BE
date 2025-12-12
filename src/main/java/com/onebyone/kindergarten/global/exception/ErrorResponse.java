package com.onebyone.kindergarten.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {
  private final String code;
  private final String message;

  @Builder
  public ErrorResponse(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public static ErrorResponse buildError(ErrorCodes errorCodes) {
    return ErrorResponse.builder()
        .code(errorCodes.getCode())
        .message(errorCodes.getMessage())
        .build();
  }
}
