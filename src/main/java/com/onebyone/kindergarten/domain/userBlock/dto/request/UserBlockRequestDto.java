package com.onebyone.kindergarten.domain.userBlock.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBlockRequestDto {
  private String targetUserEmail;
}
