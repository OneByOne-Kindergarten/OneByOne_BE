package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponseDTO {
  private String accessToken;
  private String refreshToken;

  @Builder
  public SignUpResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
