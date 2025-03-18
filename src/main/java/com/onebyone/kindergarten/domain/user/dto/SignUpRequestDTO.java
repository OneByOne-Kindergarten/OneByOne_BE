package com.onebyone.kindergarten.domain.user.dto;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.entity.UserProvider;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpRequestDTO {
    private String email;
    private String password;
    private UserProvider provider;
    private Long providerId = null;
    private String nickname;
    private UserRole role;
    private String profileImageUrl;

    @Builder
    public SignUpRequestDTO(String email, String password, UserProvider provider, String nickname, UserRole role, String profileImageUrl) {
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
                .providerId(providerId)
                .nickname(nickname)
                .role(role)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
