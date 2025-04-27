package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "user")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false, unique = false)
    private String password; // 비밀번호

    @Column(name = "fcm_token")
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private UserProvider provider; // 제공자 - 일반, 구글, 애플

    @Column(name = "kakao_id")
    private Long kakaoProviderId;  // 카카오 로그인 회사 당 할당받는 유저 pk

    @Column(name = "naver_id")
    private String naverProviderId;  // 카카오 로그인 회사 당 할당받는 유저 pk

    @Column(nullable = false)
    private String nickname; // 닉네임 - 랜덤 생성

    @Enumerated(EnumType.STRING)
    private UserRole role; // 역할 - 선생님, 예비, 관리자

    @Column(nullable = true)
    private String profileImageUrl; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 상태 - 활성, 정지, 삭제

    @Column(name = "career")
    private String career; // 커리어

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id")
    private Kindergarten Kindergarten;

    public static User registerKakao(String email, String password, Long kakaoProviderId, String nickname, UserRole role, String profileImageUrl) {
        return User.builder()
                .email(email)
                .password(password)
                .provider(UserProvider.KAKAO)
                .kakaoProviderId(kakaoProviderId)
                .nickname(nickname)
                .role(role)
                .profileImageUrl(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User registerNaver(String email, String password, String naverProviderId, String nickname, UserRole role, String profileImageUrl) {
        return User.builder()
                .email(email)
                .password(password)
                .provider(UserProvider.NAVER)
                .naverProviderId(naverProviderId)
                .nickname(nickname)
                .role(role)
                .profileImageUrl(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getTotalCareer() {
        return career;
    }

    public void updateCareer(String career) {
        this.career = career;
    }
}

