package com.onebyone.kindergarten.domain.user.dto;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtUserInfoDto {
  private Long userId;
  private UserRole role;
}
