package com.onebyone.kindergarten.domain.user.dto.request;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.entity.UserProvider;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class SignUpRequestDTO {
  private String email;
  private String password;
  private UserProvider provider;
  private String nickname;
  private UserRole role;
  private String profileImageUrl;

  @Builder
  public SignUpRequestDTO(
      String email,
      String password,
      UserProvider provider,
      String nickname,
      UserRole role,
      String profileImageUrl) {
    this.email = email;
    this.password = password;
    this.provider = provider;
    this.nickname = nickname;
    this.role = role;
    this.profileImageUrl = profileImageUrl;
  }

  public User toEntity(String encodedPassword) {
    return User.builder()
        .email(email)
        .password(encodedPassword)
        .provider(provider)
        .nickname(nickname)
        .role(role)
        .status(UserStatus.ACTIVE)
        .profileImageUrl(profileImageUrl)
        .build();
  }
}
