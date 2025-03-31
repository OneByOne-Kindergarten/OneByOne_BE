package com.onebyone.kindergarten.domain.reports.controller;

import com.onebyone.kindergarten.domain.reports.dto.request.ReportSearchDTO;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.service.ReportService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/report")
@Tag(name = "신고 관리", description = "신고 관리 API (관리자용)")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @PatchMapping("/{reportId}/status")
    @Operation(summary = "신고 처리", description = "신고를 처리합니다.")
    public ResponseDto<ReportResponseDTO> processReport(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status
    ) {
        return ResponseDto.success(reportService.processReport(reportId, status));
    }

    @GetMapping
    @Operation(summary = "전체 신고 목록 조회", description = "모든 신고 내역을 조회합니다.")
    public PageResponseDTO<ReportResponseDTO> getAllReports(
            ReportSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(reportService.getAllReports(searchDTO, pageable));
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "신고 상세 조회", description = "신고 상세 내역을 조회합니다.")
    public ResponseDto<ReportResponseDTO> getReportDetail(
            @PathVariable Long reportId
    ) {
        return ResponseDto.success(reportService.getReportDetail(reportId));
    }

} 