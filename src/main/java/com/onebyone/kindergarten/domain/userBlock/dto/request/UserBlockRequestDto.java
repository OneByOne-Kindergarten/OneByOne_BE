package com.onebyone.kindergarten.domain.userBlock.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBlockRequestDto {
  private String targetUserEmail;
}
