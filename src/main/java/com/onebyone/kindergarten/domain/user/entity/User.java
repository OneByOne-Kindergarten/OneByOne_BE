package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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
    private Long kakaoProviderId; // 카카오 로그인 회사 당 할당받는 유저 pk

    @Column(name = "naver_id")
    private String naverProviderId; // 네이버 로그인 회사 당 할당받는 유저 pk

    @Column(name = "apple_id")
    private String appleProviderId; // 애플 로그인 고유 사용자 ID

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

    @Column(name = "home_shortcut", columnDefinition = "TEXT")
    private String homeShortcut; // 홈 바로가기 정보 (JSON 형태로 저장)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id")
    private Kindergarten Kindergarten;

    @Column(name = "previous_deleted_at")
    private LocalDateTime previousDeletedAt;

    @Column(name = "all_notifications_enabled")
    @Builder.Default
    private Boolean allNotificationsEnabled = true;

    @Column(name = "community_notifications_enabled")
    @Builder.Default
    private Boolean communityNotificationsEnabled = true;

    @Column(name = "event_notifications_enabled")
    @Builder.Default
    private Boolean eventNotificationsEnabled = true;

    @Column(name = "has_written_review")
    @Builder.Default
    private Boolean hasWrittenReview = false;

    public boolean hasNotificationEnabled(NotificationSetting setting) {
        return switch (setting) {
            case ALL_NOTIFICATIONS -> allNotificationsEnabled == null || allNotificationsEnabled;
            case COMMUNITY_NOTIFICATIONS -> communityNotificationsEnabled == null || communityNotificationsEnabled;
            case EVENT_NOTIFICATIONS -> eventNotificationsEnabled == null || eventNotificationsEnabled;
        };
    }

    public void setNotificationSettings(Set<NotificationSetting> enabledSettings) {
        this.allNotificationsEnabled = enabledSettings.contains(NotificationSetting.ALL_NOTIFICATIONS);
        this.communityNotificationsEnabled = enabledSettings.contains(NotificationSetting.COMMUNITY_NOTIFICATIONS);
        this.eventNotificationsEnabled = enabledSettings.contains(NotificationSetting.EVENT_NOTIFICATIONS);
        this.updatedAt = LocalDateTime.now();
    }

    public static User registerKakao(String email, String password, Long kakaoProviderId, String nickname,
            UserRole role, String profileImageUrl) {
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

    public static User registerNaver(String email, String password, String naverProviderId, String nickname,
            UserRole role, String profileImageUrl) {
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

    public static User registerApple(String email, String password, String appleProviderId, String nickname,
            UserRole role) {
        return User.builder()
                .email(email)
                .password(password)
                .provider(UserProvider.APPLE)
                .appleProviderId(appleProviderId)
                .nickname(nickname)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        /// 랜덤 6자리 숫자 생성
        String randomNum = String.format("%06d", (int)(Math.random() * 1000000));
        this.nickname = "D_" + randomNum;
        /// 개인정보 마스킹
        this.profileImageUrl = null;
        this.homeShortcut = null;
        this.fcmToken = null;
        this.updatedAt = LocalDateTime.now();
    }

    /// 현재 deletedAt 값을 previousDeletedAt에 저장
    public void restore() {
        this.previousDeletedAt = this.deletedAt;
        this.status = UserStatus.ACTIVE;
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFcmToken(String fcmToken) {
        // FCM 토큰 유효성 검증
        if (fcmToken != null && !fcmToken.trim().isEmpty()) {
            String trimmedToken = fcmToken.trim();
            // 최소 길이 및 기본 패턴 검증
            if (trimmedToken.length() >= 140 && trimmedToken.matches("^[a-zA-Z0-9_:.-]+$")) {
                this.fcmToken = trimmedToken;
                this.updatedAt = LocalDateTime.now();
            } else {
                this.fcmToken = null;
            }
        } else {
            this.fcmToken = null;
        }
    }

    public String getTotalCareer() {
        return career;
    }

    public void updateCareer(String career) {
        this.career = career;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateHomeShortcut(String homeShortcutJson) {
        this.homeShortcut = homeShortcutJson;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUserRole(UserRole role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRestoredUser() {
        return this.previousDeletedAt != null;
    }

    public void markAsReviewWriter() {
        this.hasWrittenReview = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasWrittenReview() {
        return this.hasWrittenReview != null && this.hasWrittenReview;
    }

    /// 유저 상태 변경 (관리자용)
    public void updateStatus(UserStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();

        if (status == UserStatus.SUSPENDED) {
            // TODO : 정지 시 특별한 처리가 필요하다면 여기에 추가
        } else if (status == UserStatus.ACTIVE) {
            // TODO : 활성화 시 특별한 처리가 필요하다면 여기에 추가
        }
    }
}
