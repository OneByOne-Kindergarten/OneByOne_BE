package com.onebyone.kindergarten.domain.pushNotification.service;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.*;
import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationRequestDTO;
import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationResponseDTO;
import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.pushNotification.repository.PushNotificationRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final PushNotificationRepository pushNotificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /// 알림 전송 여부 확인 ( 내부 메서드 )
    private boolean shouldSendNotification(User user, NotificationType type) {
        /// NotificationType null 체크
        if (type == null) {
            log.warn("NotificationType is null - userId: {}", user.getId());
            return false;
        }
        
        /// 사용자가 전체 알림을 비활성화한 경우 모든 알림 차단
        if (!user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS)) {
            return false;
        }

        /// 알림 타입에 따라 특정 설정 확인
        return switch (type) {
            case REVIEW, COMMENT, LIKE -> user.hasNotificationEnabled(NotificationSetting.COMMUNITY_NOTIFICATIONS);
            case SYSTEM, NOTICE -> user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS);
        };
    }

    /// 푸시 알림 저장 (FCM 발송하지 않음)
    @Transactional
    public void savePushNotification(PushNotificationRequestDTO requestDTO) {
        // 사용자 조회
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));

        // 알림 설정 확인 및 FCM 토큰 유효성 검증
        boolean shouldSend = shouldSendNotification(user, requestDTO.getType());
        String userFcmToken = user.getFcmToken();
        boolean hasValidToken = isValidFcmToken(userFcmToken);
        
        // 알림 설정이 꺼져있거나 FCM 토큰이 없는 경우
        // → 알림은 저장하되 전송 완료(isSent=true)로 표시
        // → 앱 내 알림 목록에서는 확인 가능, 푸시 알림만 전송 안 함
        if (!shouldSend || !hasValidToken) {
            String reason = !shouldSend ? "알림 설정 비활성화" : "FCM 토큰 없음";
            log.debug("{} - 사용자 ID: {}, 알림은 저장하되 푸시 전송 스킵", reason, user.getId());
            
            PushNotification notification = PushNotification.builder()
                    .user(user)
                    .fcmToken(null)
                    .title(requestDTO.getTitle())
                    .message(requestDTO.getMessage())
                    .type(requestDTO.getType())
                    .targetId(requestDTO.getTargetId())
                    .isRead(false)
                    .isSent(true) // 전송 완료로 표시하여 스케줄러가 재시도하지 않도록
                    .groupKey(requestDTO.getGroupKey())
                    .groupCount(requestDTO.getGroupCount() != null ? requestDTO.getGroupCount() : 1)
                    .build();
            
            pushNotificationRepository.save(notification);
            log.debug("푸시 전송 스킵 알림 저장 완료 (사유: {}) - 사용자 ID: {}, 타입: {}", 
                reason, user.getId(), requestDTO.getType());
            return;
        }

        // 그룹키가 있는 경우, 기존 미전송 알림 확인하여 중복 방지
        if (requestDTO.getGroupKey() != null && !requestDTO.getGroupKey().isEmpty()) {
            Optional<PushNotification> existingNotification = 
                pushNotificationRepository.findFirstByUserIdAndGroupKeyAndIsSentFalseOrderByCreatedAtDesc(
                    requestDTO.getUserId(), 
                    requestDTO.getGroupKey()
                );
            
            if (existingNotification.isPresent()) {
                // 기존 미전송 알림이 있으면 중복 생성하지 않고 로그만 남김
                log.debug("중복 알림 방지 - 사용자 ID: {}, 그룹키: {}, 기존 알림 ID: {}", 
                    user.getId(), requestDTO.getGroupKey(), existingNotification.get().getId());
                return;
            }
        }

        // 알림 저장 (FCM 발송하지 않음)
        PushNotification notification = PushNotification.builder()
                .user(user)
                .fcmToken(userFcmToken)
                .title(requestDTO.getTitle())
                .message(requestDTO.getMessage())
                .type(requestDTO.getType())
                .targetId(requestDTO.getTargetId())
                .isRead(false)
                .isSent(false)

                /// 그룹화
                .groupKey(requestDTO.getGroupKey())
                .groupCount(requestDTO.getGroupCount() != null ? requestDTO.getGroupCount() : 1)
                .build();

        pushNotificationRepository.save(notification);
        log.info("푸시 알림 저장 완료 - 사용자 ID: {}, 타입: {}, 그룹키: {}", 
            user.getId(), requestDTO.getType(), requestDTO.getGroupKey());
    }

    /// FCM 통해 여러 알림 동시 전송 (비동기 처리)
    @Transactional
    public void sendAllFCMNotificationsByAsync(List<PushNotification> notifications) {
        // 성공한 알림들을 저장할 리스트 (catch 블록에서도 접근 가능하도록 외부에 선언)
        List<PushNotification> updatedNotifications = new ArrayList<>();
        
        try {
            // 알림이 비어있거나 null인 경우 처리
            if (notifications == null || notifications.isEmpty()) {
                log.info("전송할 알림이 없습니다.");
                return;
            }
            
            // 개별 메시지 목록 생성
        List<Message> messages = new ArrayList<>();
        List<PushNotification> validNotifications = new ArrayList<>();
        
        for (PushNotification notification : notifications) {
            /// 중복 전송 방지: 이미 전송된 알림은 건너뛰기
            if (notification.getIsSent() != null && notification.getIsSent()) {
                log.debug("이미 전송된 알림 발견 (ID: {}), 건너뛰기", notification.getId());
                continue;
            }
            
            if (notification.getFcmToken() == null || notification.getFcmToken().isEmpty()) {
                log.warn("FCM 토큰이 없는 알림 발견 (ID: {}), 건너뛰기", notification.getId());
                continue;
            }
            
            validNotifications.add(notification);

            // 추가 데이터 설정
            Map<String, String> data = new HashMap<>();
            data.put("type", notification.getType().name());
            if (notification.getTargetId() != null) {
                data.put("targetId", notification.getTargetId().toString());
            }

            ApsAlert alert = ApsAlert.builder()
                    .setTitle(notification.getTitle())
                    .setBody(notification.getMessage())
                    .build();

            Aps aps = Aps.builder()
                    .setAlert(alert)
                    .setSound("default")
                    ///.setBadge()
                    .build();

            // 개별 메시지 생성 - 알림 엔티티에 저장된 FCM 토큰 사용
            Message message = Message.builder()
                    .setToken(notification.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getMessage())
                            ///.setImage("이미지 URL")
                            .build())
                    .putAllData(data)
                    .setApnsConfig(
                            ApnsConfig.builder()
                                    .setAps(aps)
                                    .build()
                    )
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setTitle(notification.getTitle())
                                    .setBody(notification.getMessage())
                                    .build())
                            .build())
                    .build();

            messages.add(message);
        }
        
        // 실제 전송할 메시지가 없는 경우 종료
        if (messages.isEmpty()) {
            log.info("실제 전송할 수 있는 메시지가 없습니다.");
            return;
        }

        // 비동기 메시지 전송을 위한 Future 목록
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // 모든 메시지를 비동기로 전송
        for (Message message : messages) {
            ApiFuture<String> future = firebaseMessaging.sendAsync(message);
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

                @Override
                public void onSuccess(String messageId) {
                    completableFuture.complete(messageId);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    completableFuture.completeExceptionally(throwable);
                }
            }, MoreExecutors.directExecutor());

            futures.add(completableFuture);
        }

        // 모든 Future가 완료될 때까지 대기 (최대 30초 타임아웃)
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(30, TimeUnit.SECONDS)
                .join();
        } catch (Exception e) {
            log.error("FCM 알림 전송 중 타임아웃 또는 오류 발생: {}", e.getMessage());
            // 타임아웃이어도 개별 Future 결과는 처리
        }

        // 응답 결과 수집
        int successCount = 0;
        int failureCount = 0;

        // 각 Future의 결과 처리
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<String> future = futures.get(i);
            PushNotification notification = validNotifications.get(i);

            try {
                // 성공적으로 완료된 경우
                String messageId = future.get();
                successCount++;

                // 성공 처리
                notification.markAsSent();
                updatedNotifications.add(notification);
                log.info("FCM 알림 전송 성공: {}, 메시지 ID: {}", notification.getId(), messageId);
            } catch (InterruptedException | ExecutionException e) {
                // 실패한 경우
                failureCount++;

                // Firebase 관련 예외인지 확인
                Throwable cause = e.getCause();
                if (cause instanceof FirebaseMessagingException fme) {
                    handleFirebaseMessagingException(notification, fme);
                } else {
                    log.error("FCM 알림 전송 실패: {}, 에러: {}",
                            notification.getId(), e.getMessage());
                }
                
                // 실패한 알림도 전송 완료로 표시하여 재시도 방지
                notification.markAsSent();
                updatedNotifications.add(notification);
            }
        }

        // 알림 상태 일괄 업데이트 (별도 트랜잭션으로 처리)
        updateNotificationStatus(updatedNotifications);

        log.info("FCM 알림 배치 전송 결과 - 성공: {}, 실패: {}, 총: {}",
                successCount,
                failureCount,
                validNotifications.size());

            log.info("FCM 알림 배치 전송 요청 완료: {} 개", validNotifications.size());
        } catch (Exception e) {
            // 예외가 발생하더라도 성공한 알림들은 반드시 상태 업데이트
            if (!updatedNotifications.isEmpty()) {
                try {
                    updateNotificationStatus(updatedNotifications);
                    log.info("성공한 알림 상태 업데이트 완료: {} 개", updatedNotifications.size());
                } catch (Exception updateException) {
                    log.error("알림 상태 업데이트 중 추가 오류 발생: {}", updateException.getMessage(), updateException);
                }
            }
        }
    }

    /// 알림 상태 일괄 업데이트 (별도 트랜잭션으로 처리)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNotificationStatus(List<PushNotification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        try {
            // 알림 상태 업데이트
            pushNotificationRepository.saveAll(notifications);
            log.info("알림 상태 일괄 업데이트 완료: {} 개", notifications.size());
        } catch (Exception e) {
            log.error("알림 상태 일괄 업데이트 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /// 특정 시간 이전에 생성된 미전송 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotification> getUnsentNotificationsBeforeTime(LocalDateTime cursorTime) {
        return pushNotificationRepository.findUnsentNotificationsBeforeTime(cursorTime);
    }

    /// 사용자의 모든 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotificationResponseDTO> getUserNotifications(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));

        // 모든 알림 조회
        return pushNotificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(PushNotificationResponseDTO::from)
                .collect(Collectors.toList());
    }

    /// 현재 로그인한 사용자의 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotificationResponseDTO> getUserNotificationByUserDetails(Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);

        // 모든 알림 조회
        return pushNotificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(PushNotificationResponseDTO::from)
                .collect(Collectors.toList());
    }


    /// 사용자의 읽지 않은 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotificationResponseDTO> getUnreadNotificationsByUserDetails(Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);

        // 읽지 않은 알림 조회
        return pushNotificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(PushNotificationResponseDTO::from)
                .collect(Collectors.toList());
    }

    /// 알림 읽음 표시
    @Transactional
    public void markAsRead(Long notificationId) {

        // 알림 조회
        PushNotification notification = pushNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));

        // 읽음 처리
        notification.markAsRead();
        pushNotificationRepository.save(notification);
    }

    /// 사용자의 모든 알림 읽음 표시
    @Transactional
    public void markAllAsRead(Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);

        // 모든 알림 읽음 처리
        pushNotificationRepository.markAllAsRead(user, LocalDateTime.now());
    }

    /// 읽지 않은 알림 개수 조회
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);

        // 읽지 않은 알림 개수 조회
        return pushNotificationRepository.countByUserAndIsReadFalse(user);
    }

    /// Firebase 메시징 예외 처리 (토큰 정리 포함)
    private void handleFirebaseMessagingException(PushNotification notification, FirebaseMessagingException exception) {
        MessagingErrorCode errorCode = exception.getMessagingErrorCode();
        String errorMessage = exception.getMessage();
        
        log.error("FCM 알림 전송 실패 - 알림 ID: {}, 에러 코드: {}, 메시지: {}", 
                notification.getId(), errorCode, errorMessage);

        // 토큰 관련 오류인 경우 사용자의 FCM 토큰 정리
        switch (errorCode) {
            case UNREGISTERED:
                // 토큰이 등록되지 않음 (앱 삭제됨)
                log.warn("등록되지 않은 FCM 토큰 발견 - 사용자 ID: {}, 토큰 정리", notification.getUser().getId());
                clearUserFcmToken(notification.getUser());
                break;
                
            case INVALID_ARGUMENT:
                // 잘못된 토큰 형식
                if (errorMessage != null && errorMessage.contains("Requested entity was not found")) {
                    log.warn("유효하지 않은 FCM 토큰 발견 - 사용자 ID: {}, 토큰 정리", notification.getUser().getId());
                    clearUserFcmToken(notification.getUser());
                }
                break;
                
            case SENDER_ID_MISMATCH:
                // 잘못된 발신자 ID
                log.warn("FCM 발신자 ID 불일치 - 사용자 ID: {}, 토큰 정리", notification.getUser().getId());
                clearUserFcmToken(notification.getUser());
                break;
                
            case QUOTA_EXCEEDED:
                // 할당량 초과 - 토큰은 유효하므로 정리하지 않음
                log.warn("FCM 할당량 초과 - 알림 ID: {}", notification.getId());
                break;
                
            case UNAVAILABLE:
                // 서비스 일시 불가 - 토큰은 유효하므로 정리하지 않음
                log.warn("FCM 서비스 일시 불가 - 알림 ID: {}", notification.getId());
                break;
                
            case INTERNAL:
                // 내부 오류 - 토큰은 유효하므로 정리하지 않음
                log.warn("FCM 내부 오류 - 알림 ID: {}", notification.getId());
                break;
                
            default:
                log.error("알 수 없는 FCM 오류 - 알림 ID: {}, 에러 코드: {}", notification.getId(), errorCode);
                break;
        }
    }

    /// 사용자의 FCM 토큰 정리 (별도 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearUserFcmToken(User user) {
        try {
            User currentUser = userRepository.findById(user.getId())
                    .orElse(null);
            
            if (currentUser != null && currentUser.getFcmToken() != null) {
                currentUser.updateFcmToken(null);
                userRepository.save(currentUser);
                log.info("사용자 FCM 토큰 정리 완료 - 사용자 ID: {}", user.getId());
            }
        } catch (Exception e) {
            log.error("사용자 FCM 토큰 정리 중 오류 발생 - 사용자 ID: {}, 에러: {}", 
                    user.getId(), e.getMessage(), e);
        }
    }

    /// FCM 토큰 유효성 검증
    public boolean isValidFcmToken(String fcmToken) {
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            return false;
        }
        
        String trimmedToken = fcmToken.trim();
        if (trimmedToken.length() < 140) { // FCM 토큰은 일반적으로 140자 이상
            return false;
        }
        
        // 기본적인 문자 패턴 검증 (영숫자, 하이픈, 언더스코어, 콜론만 허용)
        return trimmedToken.matches("^[a-zA-Z0-9_:.-]+$");
    }
}