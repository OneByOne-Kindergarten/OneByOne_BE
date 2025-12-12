package com.onebyone.kindergarten.domain.user.dto;

import lombok.Data;

@Data
public class SimpleUserDTO {
  private Long userId;
  private String nickname;

  public SimpleUserDTO(Long userId, String nickname) {
    this.userId = userId;
    this.nickname = nickname;
  }
}
