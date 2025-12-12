package com.onebyone.kindergarten.domain.user.dto.request;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDTO {
  private UserRole role;
}
