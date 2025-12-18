package com.onebyone.kindergarten.domain.user.dto.request;

import lombok.Data;

@Data
public class SignInRequestDTO {
  private String email;
  private String password;
  private String fcmToken;
}
