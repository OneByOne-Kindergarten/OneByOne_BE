package com.onebyone.kindergarten.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponseDTO {
    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
