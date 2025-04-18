package com.onebyone.kindergarten.domain.notice.service;

import com.onebyone.kindergarten.domain.notice.dto.request.NoticeCreateRequestDTO;
import com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO;
import com.onebyone.kindergarten.domain.notice.entity.Notice;
import com.onebyone.kindergarten.domain.notice.exception.NoticeNotFoundException;
import com.onebyone.kindergarten.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /// 공개된 공지사항 조회
    public List<NoticeResponseDTO> getPublicNotices() {
        return noticeRepository.findPublicNoticeDtos();
    }

    /// 관리자용 전체 공지사항 조회
    public List<NoticeResponseDTO> getAllNotices() {
        return noticeRepository.findAllNoticeDtos();
    }

    /// 공지사항 생성
    @Transactional
    public NoticeResponseDTO createNotice(NoticeCreateRequestDTO dto) {

        // Entity 변환
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .isPushSend(dto.getIsPushSend())
                .isPublic(dto.getIsPublic())
                .build();

        /// TODO : 푸시 알림 여부 확인 후 푸시 알림 전송 로직 추가

        // 공지사항 저장
        noticeRepository.save(notice);
        return new NoticeResponseDTO(notice);
    }

    /// 공지사항 공개 여부 변경
    @Transactional
    public NoticeResponseDTO togglePublicStatus(Long noticeId) {

        // 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        // 공개 여부 토글
        notice.togglePublicStatus();
        return new NoticeResponseDTO(notice);
    }

}
