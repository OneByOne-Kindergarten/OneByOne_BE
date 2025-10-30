package com.onebyone.kindergarten.domain.pushNotification.service;

import com.onebyone.kindergarten.domain.pushNotification.entity.PushNotification;
import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import com.onebyone.kindergarten.domain.pushNotification.event.PushNotificationEvent;
import com.onebyone.kindergarten.domain.pushNotification.event.PushNotificationEventPublisher;
import com.onebyone.kindergarten.domain.pushNotification.repository.PushNotificationRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 알림 타입별 템플릿을 관리하고 발행하는 서비스
 * 모든 알림 발송 로직은 이 클래스를 통해 처리하여 일관성 있는 알림 메시지 형식 유지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final PushNotificationEventPublisher notificationEventPublisher;
    private final PushNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /// 메시지 템플릿 상수
    private static final String CHECK_APP_MESSAGE = " 👀 앱에서 자세히 확인해보세요!";

    /// 댓글,답글 템플릿
    private static final String COMMENT_EMOJI = "✏️";
    private static final String REPLY_EMOJI = "💬";
    private static final String LIKE_EMOJI = "❤️";

    private static final String SINGLE_COMMENT_MESSAGE = "%s %s님이 댓글을 남겼습니다.\n👀 새로운 댓글을 확인해보세요!";
    private static final String SINGLE_REPLY_MESSAGE = "%s %s님이 답글을 남겼습니다.\n👀 답글을 바로 확인해보세요!";
    private static final String GROUP_COMMENT_MESSAGE_2 = "%s %s님과 1명이 댓글을 남겼습니다.\n👀 댓글을 확인해보세요!";
    private static final String GROUP_COMMENT_MESSAGE_N = "%s %s님 외 %d명이 댓글을 남겼습니다.\n👀 댓글을 확인해보세요!";
    private static final String GROUP_REPLY_MESSAGE_2 = "%s %s님과 1명이 답글을 남겼습니다.\n👀 답글을 확인해보세요!";
    private static final String GROUP_REPLY_MESSAGE_N = "%s %s님 외 %d명이 답글을 남겼습니다.\n👀 답글을 확인해보세요!";

    /// 좋아요 템플릿
    private static final String SINGLE_LIKE_MESSAGE = "%s %s님이 회원님의 게시글을 좋아합니다\n👀 지금 바로 확인해보세요!";
    private static final String GROUP_LIKE_MESSAGE_2 = "%s %s님과 1명이 회원님의 게시글을 좋아합니다\n👀 지금 바로 확인해보세요!";
    private static final String GROUP_LIKE_MESSAGE_N = "%s %s님 외 %d명이 회원님의 게시글을 좋아합니다\n👀 지금 바로 확인해보세요!";

    /// 그룹화 시간 설정 (20분 이내의 알림은 그룹화)
    /// TODO : 테스트를 위해 현재 3분으로 사용 추후 20분으로 변경 필요
    private static final Duration GROUP_TIME_WINDOW = Duration.ofMinutes(3);

    /**
     * 댓글 알림을 발송합니다.
     */
    @Transactional
    public void sendCommentNotification(Long targetUserId, User actionUser, String content, boolean isReply, Long postId) {
        if (!targetUserId.equals(actionUser.getId())) {
            String groupKey = (isReply ? "REPLY_" : "COMMENT_") + postId;
            LocalDateTime sinceTime = LocalDateTime.now().minus(GROUP_TIME_WINDOW);
            Optional<PushNotification> existingNotification = findGroupableNotification(targetUserId, groupKey, sinceTime);
            if (existingNotification.isPresent()) {
                handleGroupedNotification(existingNotification.get(), actionUser, isReply);
            } else {
                createNewCommentNotification(targetUserId, actionUser, isReply, postId, groupKey);
            }
        }
    }

    /**
     * 좋아요 알림을 발송합니다.
     */
    @Transactional
    public void sendLikeNotification(Long targetUserId, User actionUser, String contentTitle, Long targetId) {
        if (!targetUserId.equals(actionUser.getId())) {
            String groupKey = "LIKE_" + targetId;
            LocalDateTime sinceTime = LocalDateTime.now().minus(GROUP_TIME_WINDOW);
            Optional<PushNotification> existingNotification = findGroupableNotification(targetUserId, groupKey, sinceTime);
            if (existingNotification.isPresent()) {
                handleGroupedLikeNotification(existingNotification.get(), actionUser);
            } else {
                createNewLikeNotification(targetUserId, actionUser, targetId, groupKey);
            }
        }
    }

    /**
     * 문의 답변 알림을 발송합니다.
     */
    public void sendInquiryAnswerNotification(Long targetUserId, String inquiryTitle, Long inquiryId) {
        String message = "✅ 문의하신 내용에 답변이 등록되었습니다." + CHECK_APP_MESSAGE;
        PushNotificationEvent event = createNotificationEvent(
                targetUserId,
                "문의 답변 알림",
                message,
                NotificationType.SYSTEM,
                inquiryId,
                null
        );
        notificationEventPublisher.publish(event);
        log.debug("문의 답변 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 공지사항 알림을 발송합니다.
     */
    public void sendNoticeNotification(Long targetUserId, String noticeTitle, String noticeContent, Long noticeId) {
        String message = "📢 [" + noticeTitle + "]\n" + CHECK_APP_MESSAGE;
        PushNotificationEvent event = createNotificationEvent(
                targetUserId,
                "공지사항",
                message,
                NotificationType.NOTICE,
                noticeId,
                null
        );
        notificationEventPublisher.publish(event);
        log.debug("공지사항 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 시스템 알림을 발송합니다.
     */
    public void sendSystemNotification(Long targetUserId, String title, String content, Long targetId) {
        String message = "🔔 [" + title + "]\n" + CHECK_APP_MESSAGE;
        PushNotificationEvent event = createNotificationEvent(
                targetUserId,
                "시스템 알림",
                message,
                NotificationType.SYSTEM,
                targetId,
                null
        );
        notificationEventPublisher.publish(event);
        log.debug("시스템 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 모든 활성 사용자에게 공지사항 알림을 발송합니다.
     * 사용자별 알림 설정을 확인하여 알림을 받을 사용자에게만 전송합니다.
     * 배치 처리로 스레드 풀 포화를 방지합니다.
     */
    public void sendNoticeNotificationToAllUsers(String noticeTitle, String noticeContent, Long noticeId) {
        try {
            List<User> activeUsers = userRepository.findAllActiveUsers();
            
            log.info("공지사항 푸시 알림 전송 시작 - 공지 ID: {}, 전체 사용자 수: {}", 
                noticeId, activeUsers.size());

            int sentCount = 0;
            int skippedCount = 0;
            final int BATCH_SIZE = 20; // 배치 크기 설정 (스레드 풀 용량 35개 고려)

            for (int i = 0; i < activeUsers.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, activeUsers.size());
                List<User> batch = activeUsers.subList(i, endIndex);
                
                log.debug("배치 처리 중: {}/{} (배치 크기: {})", 
                    endIndex, activeUsers.size(), batch.size());

                /// 푸시 알림 전송
                for (User user : batch) {
                    try {
                        if (shouldSendNoticeNotification(user)) {
                            sendNoticeNotification(
                                user.getId(),
                                noticeTitle,
                                noticeContent,
                                noticeId
                            );
                            sentCount++;
                        } else {
                            skippedCount++;
                            log.debug("알림 설정으로 인해 푸시 전송 스킵 - 사용자 ID: {}", user.getId());
                        }
                    } catch (Exception e) {
                        log.error("사용자 {}에게 공지사항 푸시 알림 전송 실패: {}", 
                            user.getId(), e.getMessage(), e);
                        skippedCount++;
                    }
                }

                /// 배치 간 대기 (스레드 풀 여유 확보)
                if (endIndex < activeUsers.size()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("배치 처리 중 인터럽트 발생");
                        break;
                    }
                }
            }

            log.info("공지사항 푸시 알림 전송 완료 - 공지 ID: {}, 전송: {}명, 스킵: {}명", 
                noticeId, sentCount, skippedCount);
        } catch (Exception e) {
            log.error("공지사항 푸시 알림 전송 중 오류 발생 - 공지 ID: {}, 오류: {}", 
                noticeId, e.getMessage(), e);
        }
    }

    /**
     * 사용자가 공지사항 알림을 받을 수 있는지 확인합니다.
     */
    private boolean shouldSendNoticeNotification(User user) {
        /// 전체 알림이 비활성화된 경우
        if (!user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS)) {
            return false;
        }
        
        /// 공지사항은 EVENT_NOTIFICATIONS 설정 확인
        return user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS);
    }


    /// ===== 헬퍼 메서드 =====


    /**
     * 알림 그룹화 가능 여부를 확인합니다.
     * 미전송 알림 중에서만 그룹화 대상을 찾습니다.
     */
    private Optional<PushNotification> findGroupableNotification(
            Long userId,
            String groupKey,
            LocalDateTime sinceTime) {
        return notificationRepository.findFirstByUserIdAndGroupKeyAndIsSentFalseOrderByCreatedAtDesc(userId, groupKey)
                .filter(notification -> notification.getCreatedAt().isAfter(sinceTime));
    }

    /**
     * 알림 그룹화 처리
     */
    private void handleGroupedNotification(PushNotification notification, User actionUser, boolean isReply) {
        notification.increaseGroupCount();
        String updatedMessage = createGroupedMessage(actionUser.getNickname(), notification.getGroupCount(), isReply);
        notification.updateGroupMessage(updatedMessage);
        notificationRepository.save(notification);
        log.debug("댓글 알림 그룹화 (count={}): 대상자={}", notification.getGroupCount(), notification.getUser().getId());
    }

    /**
     * 좋아요 알림 그룹화 처리
     */
    private void handleGroupedLikeNotification(PushNotification notification, User actionUser) {
        notification.increaseGroupCount();
        String updatedMessage = createGroupedLikeMessage(actionUser.getNickname(), notification.getGroupCount());
        notification.updateGroupMessage(updatedMessage);
        notificationRepository.save(notification);
        log.debug("좋아요 알림 그룹화 (count={}): 대상자={}", notification.getGroupCount(), notification.getUser().getId());
    }

    /**
     * 새로운 댓글 알림 생성
     */
    private void createNewCommentNotification(Long targetUserId, User actionUser, boolean isReply, Long postId, String groupKey) {
        String emoji = isReply ? REPLY_EMOJI : COMMENT_EMOJI;
        String title = isReply ? "답글 알림" : "댓글 알림";
        String message = String.format(
                isReply ? SINGLE_REPLY_MESSAGE : SINGLE_COMMENT_MESSAGE,
                emoji,
                actionUser.getNickname()
        );

        PushNotificationEvent event = createNotificationEvent(
                targetUserId,
                title,
                message,
                NotificationType.COMMENT,
                postId,
                groupKey
        );

        notificationEventPublisher.publish(event);
        log.debug("댓글 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 새로운 좋아요 알림 생성
     */
    private void createNewLikeNotification(Long targetUserId, User actionUser, Long targetId, String groupKey) {
        String message = String.format(SINGLE_LIKE_MESSAGE, LIKE_EMOJI, actionUser.getNickname());

        PushNotificationEvent event = createNotificationEvent(
                targetUserId,
                "좋아요 알림",
                message,
                NotificationType.LIKE,
                targetId,
                groupKey
        );

        notificationEventPublisher.publish(event);
        log.debug("좋아요 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 그룹화된 메시지 생성
     */
    private String createGroupedMessage(String actorName, int count, boolean isReply) {
        String emoji = isReply ? REPLY_EMOJI : COMMENT_EMOJI;
        if (count == 2) {
            return String.format(
                    isReply ? GROUP_REPLY_MESSAGE_2 : GROUP_COMMENT_MESSAGE_2,
                    emoji,
                    actorName
            );
        }
        return String.format(
                isReply ? GROUP_REPLY_MESSAGE_N : GROUP_COMMENT_MESSAGE_N,
                emoji,
                actorName,
                count - 1
        );
    }

    /**
     * 그룹화된 좋아요 메시지 생성
     */
    private String createGroupedLikeMessage(String actorName, int count) {
        if (count == 2) {
            return String.format(GROUP_LIKE_MESSAGE_2, LIKE_EMOJI, actorName);
        }
        return String.format(GROUP_LIKE_MESSAGE_N, LIKE_EMOJI, actorName, count - 1);
    }

    /**
     * 알림 이벤트 생성
     */
    private PushNotificationEvent createNotificationEvent(
            Long userId,
            String title,
            String message,
            NotificationType type,
            Long targetId,
            String groupKey) {
        PushNotificationEvent event = new PushNotificationEvent(userId, title, message, type, targetId);
        if (groupKey != null) {
            event.setGroupKey(groupKey);
        }
        return event;
    }
} 