package com.onebyone.kindergarten.domain.pushNotification.event;

import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationRequestDTO;
import com.onebyone.kindergarten.domain.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/// 비즈니스 로직에서 발행된 푸시 알림 이벤트를 수신하여 푸시 알림 이벤트 리스너
/// Async 어노테이션을 통해 비동기적으로 처리되어 메인 비즈니스 로직의 응답 시간에 영향을 주지 않음
@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationEventListener {
  private final PushNotificationService pushNotificationService;

  /// 푸시 알림 이벤트 처리 메소드
  /// 이벤트를 수신하면 알림 정보를 DB에 저장하고, 이후 스케줄러가 주기적으로 미전송 알림을 발송
  @Async
  @EventListener
  public void handlePushNotificationEvent(PushNotificationEvent event) {
    try {
      log.debug("푸시 알림 이벤트 수신: userId={}, title={}", event.getUserId(), event.getTitle());
      PushNotificationRequestDTO requestDTO =
          PushNotificationRequestDTO.builder()
              .userId(event.getUserId())
              .title(event.getTitle())
              .message(event.getMessage())
              .type(event.getType())
              .targetId(event.getTargetId())
              .groupKey(event.getGroupKey())
              .groupCount(event.getGroupCount())
              .build();
      pushNotificationService.savePushNotification(requestDTO);
      log.info("푸시 알림 이벤트 처리 완료: userId={}, title={}", event.getUserId(), event.getTitle());
    } catch (Exception e) {
      log.error("푸시 알림 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
