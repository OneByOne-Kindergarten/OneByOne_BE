package com.onebyone.kindergarten.domain.reports.dto.response;

import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportResponseDTO {
    private Long id;
    private String reporterNickname;
    private Long targetId;
    private ReportTargetType targetType;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;

    public static ReportResponseDTO fromEntity(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.id = report.getId();
        dto.reporterNickname = report.getReporter().getNickname();
        dto.targetId = report.getTargetId();
        dto.targetType = report.getTargetType();
        dto.reason = report.getReason();
        dto.status = report.getStatus();
        dto.createdAt = report.getCreatedAt();
        return dto;
    }
} 