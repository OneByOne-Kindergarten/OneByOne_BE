package com.onebyone.kindergarten.domain.reports.controller;

import com.onebyone.kindergarten.domain.reports.dto.request.CreateReportRequestDTO;
import com.onebyone.kindergarten.domain.reports.dto.request.ReportSearchDTO;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.service.ReportService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Tag(name = "Report", description = "신고하기 API")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @Operation(summary = "신고하기", description = "게시글이나 댓글을 신고합니다.")
    public ResponseDto<ReportResponseDTO> createReport(
            @Valid @RequestBody CreateReportRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(reportService.createReport(dto, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @Operation(summary = "내 신고 목록", description = "자신이 신고한 내역을 조회합니다.")
    public PageResponseDTO<ReportResponseDTO> getMyReports(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(reportService.getMyReports(userDetails.getUsername(), pageable));
    }

    @PatchMapping("/admin/{reportId}/status")
    @Operation(summary = "신고 처리", description = "신고를 처리합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<ReportResponseDTO> processReport(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status
    ) {
        return ResponseDto.success(reportService.processReport(reportId, status));
    }

    @GetMapping("/admin")
    @Operation(summary = "전체 신고 목록 조회", description = "모든 신고 내역을 조회합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponseDTO<ReportResponseDTO> getAllReports(
            ReportSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(reportService.getAllReports(searchDTO, pageable));
    }

    @GetMapping("/admin/{reportId}")
    @Operation(summary = "신고 상세 조회", description = "신고 상세 내역을 조회합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<ReportResponseDTO> getReportDetail(
            @PathVariable Long reportId
    ) {
        return ResponseDto.success(reportService.getReportDetail(reportId));
    }
}
