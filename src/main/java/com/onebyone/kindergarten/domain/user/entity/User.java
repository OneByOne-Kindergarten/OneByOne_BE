package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "login_type")
@EntityListeners(AuditingEntityListener.class)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID

    @Column(nullable = false, unique = false)
    private String password; // 비밀번호

    @Column(nullable = true)
    private String fcmToken; // FCM 토큰

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String nickname; // 닉네임 - 랜덤 생성

    @Enumerated(EnumType.STRING)
    private UserRole role; // 역할 - 선생님, 예비, 관리자

    @Column(nullable = true)
    private String profileImageUrl; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 상태 - 활성, 정지, 삭제

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 가입일

    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정일
}

