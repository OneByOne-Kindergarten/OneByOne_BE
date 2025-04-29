package com.onebyone.kindergarten.domain.pushNotification.repository;

import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
    List<PushNotification> findByUserOrderByCreatedAtDesc(User user);
    
    List<PushNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    @Modifying
    @Query("UPDATE push_notification p SET p.isRead = true WHERE p.user = :user")
    void markAllAsRead(@Param("user") User user);
    
    long countByUserAndIsReadFalse(User user);
    
    // 특정 시간 이전에 생성된 미전송 알림 조회
    @Query("SELECT p FROM push_notification p WHERE p.isSent = false AND p.createdAt <= :cursorTime ORDER BY p.createdAt ASC")
    List<PushNotification> findUnsentNotificationsBeforeTime(@Param("cursorTime") LocalDateTime cursorTime);
    
    // 그룹 키로 가장 최근 알림 찾기
    Optional<PushNotification> findFirstByUserIdAndGroupKeyOrderByCreatedAtDesc(Long userId, String groupKey);
} 