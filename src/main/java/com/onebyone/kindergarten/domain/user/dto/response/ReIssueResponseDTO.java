package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ReIssueResponseDTO {
  private String accessToken;
  private String refreshToken;

  @Builder
  public ReIssueResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
