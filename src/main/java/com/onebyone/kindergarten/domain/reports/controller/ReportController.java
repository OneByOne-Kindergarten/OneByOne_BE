package com.onebyone.kindergarten.domain.reports.controller;

import com.onebyone.kindergarten.domain.reports.dto.request.CreateReportRequestDTO;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.service.ReportService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Tag(name = "신고하기", description = "신고하기 API (사용자용)")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고하기", description = "게시글이나 댓글을 신고합니다.")
    public ResponseDto<ReportResponseDTO> createReport(
            @Valid @RequestBody CreateReportRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(reportService.createReport(dto, Long.valueOf(userDetails.getUsername())));
    }

    @GetMapping("/my")
    @Operation(summary = "내 신고 목록", description = "자신이 신고한 내역을 조회합니다.")
    public PageResponseDTO<ReportResponseDTO> getMyReports(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(reportService.getMyReports(Long.valueOf(userDetails.getUsername()), pageable));
    }

}
