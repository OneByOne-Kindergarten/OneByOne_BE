package com.onebyone.kindergarten.domain.userBlock.dto.response;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlockedUserResponseDto {
    private final String email;
    private final String nickname;
    private final UserRole userRole;
    private final String career;
    private final LocalDateTime blockedAt;

    public BlockedUserResponseDto(String email, String nickname, UserRole userRole, String career, LocalDateTime blockedAt) {
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
        this.career = career;
        this.blockedAt = blockedAt;
    }
}