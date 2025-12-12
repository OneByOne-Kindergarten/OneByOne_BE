package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Data;

@Data
public class AppleUserResponse {
  private String sub; // 애플 고유 사용자 ID
  private String email;
  private String name;
  private Boolean email_verified;
  private Boolean is_private_email; // 이메일 숨기기 여부
}
