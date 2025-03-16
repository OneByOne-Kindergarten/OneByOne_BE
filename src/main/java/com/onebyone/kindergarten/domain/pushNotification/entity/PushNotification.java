package com.onebyone.kindergarten.domain.pushNotification.entity;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity(name = "push_notification")
public class PushNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 수신자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String message; // 내용

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 타입 - 리뷰, 댓글, 좋아요, 시스템

    private Boolean isRead = false; // 읽음 여부
}
