package com.onebyone.kindergarten.domain.notice.controller;

import com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO;
import com.onebyone.kindergarten.domain.notice.service.NoticeService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@Tag(name = "공지사항", description = "공지사항 API")
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping
  @Operation(summary = "공개 공지사항 목록 조회", description = "공개된 공지사항 목록을 조회합니다.")
  public ResponseDto<List<NoticeResponseDTO>> getPublicNotices() {
    return ResponseDto.success(noticeService.getPublicNotices());
  }
}
