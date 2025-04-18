package com.onebyone.kindergarten.domain.pushNotification.service;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationRequestDTO;
import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationResponseDTO;
import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.pushNotification.exception.NotificationException;
import com.onebyone.kindergarten.domain.pushNotification.repository.PushNotificationRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final PushNotificationRepository pushNotificationRepository;
    private final UserRepository userRepository;

    /// 푸시 알림 저장 (FCM 발송하지 않음)
    @Transactional
    public void savePushNotification(PushNotificationRequestDTO requestDTO) {
        // 사용자 조회
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        // 알림 저장 (FCM 발송하지 않음)
        PushNotification notification = PushNotification.builder()
                .user(user)
                .fcmToken(user.getFcmToken()) // FCM 토큰을 알림 엔티티에 직접 저장
                .title(requestDTO.getTitle())
                .message(requestDTO.getMessage())
                .type(requestDTO.getType())
                .targetId(requestDTO.getTargetId())
                .isRead(false)
                .isSent(false)
                .build();

        pushNotificationRepository.save(notification);
    }

    /// FCM 통해 여러 알림 동시 전송 (비동기 처리)
    @Transactional
    public void sendAllFCMNotificationsByAsync(List<PushNotification> notifications) {

        // 알림이 비어있거나 null인 경우 처리
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        // FCM 토큰이 있는 알림만 필터링
        List<PushNotification> validNotifications = new ArrayList<>();

        for (PushNotification notification : notifications) {
            // 각 알림에 직접 저장된 FCM 토큰 사용
            if (notification.getFcmToken() != null && !notification.getFcmToken().isEmpty()) {
                validNotifications.add(notification);
            } else {
                log.warn("FCM 토큰이 없어 알림을 전송할 수 없습니다. notificationId: {}, userId: {}",
                        notification.getId(), notification.getUser().getId());
            }
        }

        if (validNotifications.isEmpty()) {
            log.info("전송할 수 있는 FCM 토큰이 없습니다.");
            return;
        }

        // 개별 메시지 목록 생성
        List<Message> messages = new ArrayList<>();
        for (PushNotification notification : validNotifications) {
            // 추가 데이터 설정
            Map<String, String> data = new HashMap<>();
            data.put("type", notification.getType().name());
            if (notification.getTargetId() != null) {
                data.put("targetId", notification.getTargetId().toString());
            }

            // 개별 메시지 생성 - 알림 엔티티에 저장된 FCM 토큰 사용
            Message message = Message.builder()
                    .setToken(notification.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getMessage())
                            .build())
                    .putAllData(data)
                    /// TODO : AOS, IOS Config 추가 필요
                    .build();

            messages.add(message);
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

        // 모든 Future가 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 응답 결과 수집
        int successCount = 0;
        int failureCount = 0;
        List<PushNotification> updatedNotifications = new ArrayList<>();

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

                // 실패 처리
                log.error("FCM 알림 전송 실패: {}, 에러: {}",
                        notification.getId(), e.getMessage());
            }
        }

        // 알림 상태 일괄 업데이트 (별도 트랜잭션으로 처리)
        updateNotificationStatus(updatedNotifications);

        log.info("FCM 알림 배치 전송 결과 - 성공: {}, 실패: {}, 총: {}",
                successCount,
                failureCount,
                validNotifications.size());

        log.info("FCM 알림 배치 전송 요청 완료: {} 개", validNotifications.size());
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
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        // 모든 알림 조회
        return pushNotificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(PushNotificationResponseDTO::from)
                .collect(Collectors.toList());
    }

    /// 현재 로그인한 사용자의 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotificationResponseDTO> getUserNotificationByUserDetails(UserDetails userDetails) {

        // 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        // 모든 알림 조회
        return pushNotificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(PushNotificationResponseDTO::from)
                .collect(Collectors.toList());
    }


    /// 사용자의 읽지 않은 알림 조회
    @Transactional(readOnly = true)
    public List<PushNotificationResponseDTO> getUnreadNotificationsByUserDetails(UserDetails userDetails) {

        // 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new NotificationException("알림을 찾을 수 없습니다."));

        // 읽음 처리
        notification.markAsRead();
        pushNotificationRepository.save(notification);
    }

    /// 사용자의 모든 알림 읽음 표시
    @Transactional
    public void markAllAsRead(UserDetails userDetails) {

        // 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        // 모든 알림 읽음 처리
        pushNotificationRepository.markAllAsRead(user);
    }

    /// 읽지 않은 알림 개수 조회
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(UserDetails userDetails) {

        // 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        // 읽지 않은 알림 개수 조회
        return pushNotificationRepository.countByUserAndIsReadFalse(user);
    }
}