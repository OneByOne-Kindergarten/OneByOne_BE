package com.onebyone.kindergarten.domain.user.dto.request;

import lombok.Data;

@Data
public class ModifyUserPasswordRequestDTO {
  private String currentPassword;
  private String newPassword;
}
