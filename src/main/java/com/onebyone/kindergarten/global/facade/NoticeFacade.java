package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.notice.dto.request.NoticeCreateRequestDTO;
import com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO;
import com.onebyone.kindergarten.domain.notice.service.NoticeService;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeFacade {
    
    private final NoticeService noticeService;
    private final NotificationTemplateService notificationTemplateService;

    /**
     * 공지사항을 생성하고 푸시 알림 전송 여부에 따라 알림을 발송합니다.
     */
    @Transactional
    public NoticeResponseDTO createNotice(NoticeCreateRequestDTO dto) {
        /// 공지사항 생성
        NoticeResponseDTO notice = noticeService.createNotice(dto);
        
        /// 푸시 알림 전송 여부 확인 후 전송
        if (dto.getIsPushSend() != null && dto.getIsPushSend()) {
            notificationTemplateService.sendNoticeNotificationToAllUsers(
                notice.getTitle(),
                notice.getContent(), 
                notice.getId()
            );
        }
        
        return notice;
    }
}
