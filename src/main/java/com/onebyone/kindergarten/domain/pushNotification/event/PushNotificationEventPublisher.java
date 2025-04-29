package com.onebyone.kindergarten.domain.pushNotification.event;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/// 푸시 알림 이벤트 발행자 클래스
/// 비즈니스 로직에서 직접 호출하여 푸시 알림 이벤트를 발행
@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    /// 푸시 알림 이벤트 발행 메소드
    public void publish(PushNotificationEvent event) {
        log.debug("푸시 알림 이벤트 발행: userId={}, title={}, groupKey={}",
                event.getUserId(), event.getTitle(), event.getGroupKey());
        eventPublisher.publishEvent(event);
    }
} 