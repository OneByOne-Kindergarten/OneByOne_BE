package com.onebyone.kindergarten.domain.pushNotification.dto;

import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PushNotificationResponseDTO {
  private Long id;
  private Long userId;
  private String title;
  private String message;
  private NotificationType type;
  private Long targetId;
  private Boolean isRead;
  private LocalDateTime createdAt;

  /// 그룹화
  private String groupKey;
  private Integer groupCount;

  public static PushNotificationResponseDTO from(PushNotification entity) {
    return PushNotificationResponseDTO.builder()
        .id(entity.getId())
        .userId(entity.getUser().getId())
        .title(entity.getTitle())
        .message(entity.getMessage())
        .type(entity.getType())
        .targetId(entity.getTargetId())
        .isRead(entity.getIsRead())
        .createdAt(entity.getCreatedAt())
        .groupKey(entity.getGroupKey())
        .groupCount(entity.getGroupCount())
        .build();
  }
}
