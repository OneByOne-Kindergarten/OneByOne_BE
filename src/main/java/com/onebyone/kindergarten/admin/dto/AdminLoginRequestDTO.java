package com.onebyone.kindergarten.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminLoginRequestDTO {
    private String username;  // Spring Security의 기본 필드명
    private String password;

    public AdminLoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
} 