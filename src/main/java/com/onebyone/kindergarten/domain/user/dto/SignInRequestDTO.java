package com.onebyone.kindergarten.domain.user.dto;

import lombok.Getter;

@Getter
public class SignInRequestDTO {
    private String email;
    private String password;
}
