package com.onebyone.kindergarten.domain.pushNotification.entity;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "push_notification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 수신자

    @Column(name = "fcm_token")
    private String fcmToken; // FCM 토큰 (전송 시점의 토큰)

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String message; // 내용

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 타입 - 리뷰, 댓글, 좋아요, 시스템

    private Boolean isRead = false; // 읽음 여부

    private Boolean isSent = false; // 전송 여부
    
    private Long targetId; // 알림 클릭 시 이동할 대상의 ID
    
    /// 알림 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }
    
    /// 알림 전송 처리
    public void markAsSent() {
        this.isSent = true;
    }
}
