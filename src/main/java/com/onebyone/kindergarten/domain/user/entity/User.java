package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public abstract class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false, unique = false)
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    private UserProvider provider; // 제공자 - 일반, 구글, 애플

    @Column(name = "provider_id")
    private Long providerId;  // 소셜 로그인 회사 당 할당받는 유저 pk

    @Column(nullable = false)
    private String nickname; // 닉네임 - 랜덤 생성

    @Enumerated(EnumType.STRING)
    private UserRole role; // 역할 - 선생님, 예비, 관리자

    @Column(nullable = true)
    private String profileImageUrl; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 상태 - 활성, 정지, 삭제
}

