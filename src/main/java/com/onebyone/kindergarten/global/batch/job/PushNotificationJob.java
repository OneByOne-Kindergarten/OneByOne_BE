package com.onebyone.kindergarten.global.batch.job;

import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.pushNotification.service.PushNotificationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationJob extends QuartzJobBean {

  private final PushNotificationService pushNotificationService;

  /// 15분마다 미전송 알림을 조회하여 FCM 발송
  /// 현재 시간 기준으로 미전송 알림을 조회하여 발송
  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    log.info("===== 푸시 알림 Job 실행 시작: {} =====", LocalDateTime.now());

    try {
      // 현재 시간을 커서로 설정
      LocalDateTime cursorTime = LocalDateTime.now();

      // 미전송 알림 조회 (트랜잭션 범위 내에서 수행)
      List<PushNotification> unsentNotifications = getUnsentNotifications(cursorTime);
      log.info("미전송 알림 수: {}", unsentNotifications.size());

      // 미전송 알림이 없는 경우 종료
      if (unsentNotifications.isEmpty()) {
        log.info("미전송 알림이 없습니다.");
        return;
      }

      // 푸시 알림 전송
      pushNotificationService.sendAllFCMNotificationsByAsync(unsentNotifications);

    } catch (Exception e) {
      log.error("푸시 알림 Job 실행 중 오류 발생: {}", e.getMessage(), e);
      throw new JobExecutionException(e);
    }

    log.info("===== 푸시 알림 Job 실행 완료: {} =====", LocalDateTime.now());
  }

  /// 미전송 알림 조회 (트랜잭션 범위 내에서 수행)
  protected List<PushNotification> getUnsentNotifications(LocalDateTime cursorTime) {
    return pushNotificationService.getUnsentNotificationsBeforeTime(cursorTime);
  }
}
