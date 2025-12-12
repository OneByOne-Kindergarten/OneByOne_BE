package com.onebyone.kindergarten.domain.user.dto.response;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.entity.UserProvider;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDTO {
  private Long id;
  private String email;
  private String nickname;
  private UserRole role;
  private UserProvider provider;
  private UserStatus status;
  private String profileImageUrl;
  private String career;
  private String kindergartenName;
  private LocalDateTime createdAt;
  private LocalDateTime deletedAt;
  private LocalDateTime previousDeletedAt;
  private boolean isRestoredUser;
  private boolean hasWrittenReview;
  private boolean allNotificationsEnabled;
  private boolean communityNotificationsEnabled;
  private boolean eventNotificationsEnabled;

  public static AdminUserResponseDTO from(User user) {
    return AdminUserResponseDTO.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole())
        .provider(user.getProvider())
        .status(user.getStatus())
        .profileImageUrl(user.getProfileImageUrl())
        .career(user.getCareer())
        .kindergartenName(user.getKindergarten() != null ? user.getKindergarten().getName() : null)
        .createdAt(user.getCreatedAt())
        .deletedAt(user.getDeletedAt())
        .previousDeletedAt(user.getPreviousDeletedAt())
        .isRestoredUser(user.isRestoredUser())
        .hasWrittenReview(user.hasWrittenReview())
        .allNotificationsEnabled(user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS))
        .communityNotificationsEnabled(
            user.hasNotificationEnabled(NotificationSetting.COMMUNITY_NOTIFICATIONS))
        .eventNotificationsEnabled(
            user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS))
        .build();
  }
}
