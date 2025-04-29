package com.onebyone.kindergarten.domain.pushNotification.event;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/// 푸시 알림 이벤트 클래스
@Getter
@RequiredArgsConstructor
public class PushNotificationEvent {
    private final Long userId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final Long targetId;

    /// 그룹화
    @Setter
    private String groupKey;
    @Setter
    private Integer groupCount = 1;

}