package com.onebyone.kindergarten.domain.reports.dto.request;

import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import lombok.Data;

@Data
public class ReportSearchDTO {
  private ReportStatus status;
  private ReportTargetType targetType;
}
