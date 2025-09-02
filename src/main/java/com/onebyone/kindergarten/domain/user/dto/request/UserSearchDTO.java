package com.onebyone.kindergarten.domain.user.dto.request;

import com.onebyone.kindergarten.domain.user.entity.UserProvider;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO {
    private String email;
    private String nickname;
    private UserRole role;
    private UserProvider provider;
    private UserStatus status;
    private String kindergartenName;
    private Boolean hasWrittenReview;
    private Boolean isRestoredUser;
}
