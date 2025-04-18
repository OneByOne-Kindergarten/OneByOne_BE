package com.onebyone.kindergarten.domain.pushNotification.dto;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequestDTO {
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private Long targetId; // 알림 클릭 시 이동할 대상의 ID (예: 게시글 ID, 리뷰 ID 등)
} 