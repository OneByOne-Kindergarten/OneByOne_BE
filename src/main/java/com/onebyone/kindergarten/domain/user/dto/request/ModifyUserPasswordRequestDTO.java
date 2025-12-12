package com.onebyone.kindergarten.domain.user.dto.request;

import lombok.Getter;

@Getter
public class ModifyUserPasswordRequestDTO {
  private String currentPassword;
  private String newPassword;
}
