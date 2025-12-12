package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Data;

@Data
public class AppleTokenResponse {
  private String access_token;
  private String token_type;
  private String expires_in;
  private String refresh_token;
  private String id_token; // 애플의 JWT 토큰
}
