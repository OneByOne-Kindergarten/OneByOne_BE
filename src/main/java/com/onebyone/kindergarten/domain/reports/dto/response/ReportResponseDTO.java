package com.onebyone.kindergarten.domain.reports.dto.response;

import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReportResponseDTO {
  private final Long id;
  private final String reporterNickname;
  private final Long targetId;
  private final ReportTargetType targetType;
  private final String reason;
  private final ReportStatus status;
  private final LocalDateTime createdAt;

  // JPQL 위한 생성자 추가
  public ReportResponseDTO(
      Long id,
      String reporterNickname,
      Long targetId,
      ReportTargetType targetType,
      String reason,
      ReportStatus status,
      LocalDateTime createdAt) {
    this.id = id;
    this.reporterNickname = reporterNickname;
    this.targetId = targetId;
    this.targetType = targetType;
    this.reason = reason;
    this.status = status;
    this.createdAt = createdAt;
  }

  public static ReportResponseDTO fromEntity(Report report) {
    return new ReportResponseDTO(
        report.getId(),
        report.getReporter().getNickname(),
        report.getTargetId(),
        report.getTargetType(),
        report.getReason(),
        report.getStatus(),
        report.getCreatedAt());
  }
}
