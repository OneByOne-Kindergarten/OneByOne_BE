package com.onebyone.kindergarten.domain.pushNotification.service;

import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import com.onebyone.kindergarten.domain.pushNotification.event.PushNotificationEventPublisher;
import com.onebyone.kindergarten.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 알림 타입별 템플릿을 관리하고 발행하는 서비스
 * 모든 알림 발송 로직은 이 클래스를 통해 처리하여 일관성 있는 알림 메시지 형식 유지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final PushNotificationEventPublisher notificationEventPublisher;
    private final String CHECK_APP_MESSAGE = " 👀 앱에서 자세히 확인해보세요!";

    /**
     * 댓글 알림을 발송합니다.
     * 
     * @param targetUserId 알림을 받을 사용자 ID
     * @param actionUser 액션을 수행한 사용자 (댓글 작성자)
     * @param content 댓글 내용 (알림에는 포함하지 않음)
     * @param isReply 답글 여부 (true: 답글, false: 일반 댓글)
     * @param PostId 게시글 ID
     */
    public void sendCommentNotification(Long targetUserId, User actionUser, String content, boolean isReply, Long PostId) {
        if (!targetUserId.equals(actionUser.getId())) {
            String emoji = isReply ? "💬" : "✏️";
            String title = (isReply ? "답글 알림" : "댓글 알림");
            String message = emoji + " " + actionUser.getNickname() + "님이 " + (isReply ? "답글" : "댓글") + "을 남겼습니다.\n" + (isReply ? "👀답글을 바로 확인해보세요!" : "👀새로운 댓글을 확인해보세요!");
            
            notificationEventPublisher.publish(
                    targetUserId,
                    title,
                    message,
                    NotificationType.COMMENT,
                    PostId
            );
            log.debug("댓글 알림 발송: 대상자={}", targetUserId);
        }
    }

    /**
     * 문의 답변 알림을 발송합니다.
     * 
     * @param targetUserId 알림을 받을 사용자 ID
     * @param inquiryTitle 문의 제목 (알림에는 포함하지 않음)
     * @param inquiryId 문의 ID
     */
    public void sendInquiryAnswerNotification(Long targetUserId, String inquiryTitle, Long inquiryId) {
        notificationEventPublisher.publish(
                targetUserId,
                "문의 답변 알림",
                "✅ 문의하신 내용에 답변이 등록되었습니다." + CHECK_APP_MESSAGE,
                NotificationType.SYSTEM,
                inquiryId
        );
        log.debug("문의 답변 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 좋아요 알림을 발송합니다.
     * 
     * @param targetUserId 알림을 받을 사용자 ID
     * @param actionUser 액션을 수행한 사용자 (좋아요 누른 사람)
     * @param contentTitle 게시글/댓글/리뷰 제목 또는 내용 (알림에는 포함하지 않음)
     * @param targetId 게시글/댓글/리뷰 ID
     */
    public void sendLikeNotification(Long targetUserId, User actionUser, String contentTitle, Long targetId) {
        if (!targetUserId.equals(actionUser.getId())) {
            String title = "좋아요 알림";
            String message = "❤️ " + actionUser.getNickname() + "님이 회원님의 게시글을 좋아합니다\n" + "👀지금 바로 확인해보세요!";
            
            notificationEventPublisher.publish(
                    targetUserId,
                    title,
                    message,
                    NotificationType.LIKE,
                    targetId
            );
            log.debug("좋아요 알림 발송: 대상자={}", targetUserId);
        }
    }

    /**
     * 공지사항 알림을 발송합니다.
     * 
     * @param targetUserId 알림을 받을 사용자 ID
     * @param noticeTitle 공지사항 제목 
     * @param noticeContent 공지사항 내용 (알림에는 포함하지 않음)
     * @param noticeId 공지사항 ID
     */
    public void sendNoticeNotification(Long targetUserId, String noticeTitle, String noticeContent, Long noticeId) {
        notificationEventPublisher.publish(
                targetUserId,
                "공지사항",
                "📢 [" + noticeTitle + "]\n" + CHECK_APP_MESSAGE,
                NotificationType.NOTICE,
                noticeId
        );
        log.debug("공지사항 알림 발송: 대상자={}", targetUserId);
    }

    /**
     * 시스템 알림을 발송합니다.
     * 
     * @param targetUserId 알림을 받을 사용자 ID
     * @param title 알림 제목
     * @param content 알림 내용 (알림에는 포함하지 않음)
     * @param targetId 알림 대상 ID
     */
    public void sendSystemNotification(Long targetUserId, String title, String content, Long targetId) {
        notificationEventPublisher.publish(
                targetUserId,
                "시스템 알림",
                "🔔 [" + title + "]\n" + CHECK_APP_MESSAGE,
                NotificationType.SYSTEM,
                targetId
        );
        log.debug("시스템 알림 발송: 대상자={}", targetUserId);
    }
} 