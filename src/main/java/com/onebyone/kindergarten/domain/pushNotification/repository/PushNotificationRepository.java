package com.onebyone.kindergarten.domain.pushNotification.repository;

import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
  List<PushNotification> findByUserOrderByCreatedAtDesc(User user);

  List<PushNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

  @Modifying
  @Query("UPDATE push_notification p SET p.isRead = true, p.updatedAt = :now WHERE p.user = :user")
  void markAllAsRead(@Param("user") User user, @Param("now") LocalDateTime now);

  long countByUserAndIsReadFalse(User user);

  // 특정 시간 이전에 생성된 미전송 알림 조회 (FCM 토큰이 있는 것만 조회)
  @Query(
      "SELECT p FROM push_notification p WHERE p.isSent = false AND p.createdAt <= :cursorTime AND p.fcmToken IS NOT NULL AND p.fcmToken <> '' ORDER BY p.createdAt ASC")
  List<PushNotification> findUnsentNotificationsBeforeTime(
      @Param("cursorTime") LocalDateTime cursorTime);

  // 그룹 키로 미전송 알림 찾기 (중복 방지용)
  Optional<PushNotification> findFirstByUserIdAndGroupKeyAndIsSentFalseOrderByCreatedAtDesc(
      Long userId, String groupKey);
}
