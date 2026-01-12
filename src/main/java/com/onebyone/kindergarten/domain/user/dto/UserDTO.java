package com.onebyone.kindergarten.domain.user.dto;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
  private Long userId;
  private String nickname;
  private String profileImageUrl;
  private String role;
  private String career;
  private KindergartenDTO kindergarten;
  private HomeShortcutsDto.Response homeShortcut;
  private boolean isRestoredUser;
  private LocalDateTime previousDeletedAt;
  private boolean allNotificationsEnabled;
  private boolean communityNotificationsEnabled;
  private boolean eventNotificationsEnabled;
  private boolean hasWrittenReview;
  private Integer favoriteCount;
  private Integer workReviewCount;
  private Integer internshipReviewCount;

  public static UserDTO from(
      User user, Integer favoriteCount, Integer workReviewCount, Integer internshipReviewCount) {
    HomeShortcutsDto homeShortcutsDto =
        user.getHomeShortcut() != null
            ? HomeShortcutsDto.fromJson(user.getHomeShortcut())
            : new HomeShortcutsDto();

    return new UserDTO(
        user.getId(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getRole().name(),
        user.getCareer(),
        user.getKindergarten() != null ? KindergartenDTO.from(user.getKindergarten()) : null,
        HomeShortcutsDto.Response.from(homeShortcutsDto),
        user.isRestoredUser(),
        user.getPreviousDeletedAt(),
        user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS),
        user.hasNotificationEnabled(NotificationSetting.COMMUNITY_NOTIFICATIONS),
        user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS),
        user.hasWrittenReview(),
        favoriteCount != null ? favoriteCount : 0,
        workReviewCount != null ? workReviewCount : 0,
        internshipReviewCount != null ? internshipReviewCount : 0);
  }
}
