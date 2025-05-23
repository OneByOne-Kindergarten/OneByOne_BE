package com.onebyone.kindergarten.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequestDTO {
    private String email;
    private String password;
    private String fcmToken;

    @Builder
    public SignInRequestDTO(String email, String password, String fcmToken) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}
