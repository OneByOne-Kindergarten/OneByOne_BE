package com.onebyone.kindergarten.domain.notice.controller;

import com.onebyone.kindergarten.domain.notice.dto.request.NoticeCreateRequestDTO;
import com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO;
import com.onebyone.kindergarten.domain.notice.service.NoticeService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.global.facade.NoticeFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
@Tag(name = "공지사항 관리", description = "공지사항 관리 API (관리자용)")
public class AdminNoticeController {
   
    private final NoticeService noticeService;
    private final NoticeFacade noticeFacade;

    @GetMapping
    @Operation(summary = "전체 공지사항 목록 조회", description = "모든 공지사항 목록을 조회합니다.")
    public ResponseDto<List<NoticeResponseDTO>> getAllNotices() {
        return ResponseDto.success(noticeService.getAllNotices());
    }

    @PostMapping
    @Operation(summary = "공지사항 작성", description = "새로운 공지사항을 작성합니다.")
    public ResponseDto<NoticeResponseDTO> createNotice(
            @Valid @RequestBody NoticeCreateRequestDTO request
    ) {
        return ResponseDto.success(noticeFacade.createNotice(request));
    }

    @PatchMapping("/{noticeId}/public-status")
    @Operation(summary = "공지사항 공개 여부 변경", description = "공지사항의 공개 여부를 변경합니다.")
    public ResponseDto<NoticeResponseDTO> togglePublicStatus(
            @PathVariable Long noticeId
    ) {
        return ResponseDto.success(noticeService.togglePublicStatus(noticeId));
    }
} 