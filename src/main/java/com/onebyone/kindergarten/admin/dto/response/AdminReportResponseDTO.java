package com.onebyone.kindergarten.admin.dto.response;

import com.onebyone.kindergarten.domain.reports.entity.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminReportResponseDTO {
    private Long id;
    private String type;
    private String reporterName;
    private String targetName;
    private String content;
    private String status;
    private LocalDateTime createdAt;

    public static AdminReportResponseDTO from(Report report) {
        return AdminReportResponseDTO.builder()
                .id(report.getId())
                .type(report.getTargetType().name())
                .reporterName(report.getReporter().getNickname())
                .targetName(report.getTargetId().toString())
                .content(report.getReason())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt())
                .build();
    }
} 