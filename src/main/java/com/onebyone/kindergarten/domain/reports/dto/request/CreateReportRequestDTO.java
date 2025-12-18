package com.onebyone.kindergarten.domain.reports.dto.request;

import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequestDTO {
  @NotNull private Long targetId;

  @NotNull private ReportTargetType targetType;

  @NotBlank private String reason;
}
