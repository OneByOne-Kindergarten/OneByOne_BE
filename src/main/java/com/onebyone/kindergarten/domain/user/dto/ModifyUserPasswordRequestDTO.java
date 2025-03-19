package com.onebyone.kindergarten.domain.user.dto;

import lombok.Getter;

@Getter
public class ModifyUserPasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
