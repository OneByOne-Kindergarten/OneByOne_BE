package com.onebyone.kindergarten.domain.reports.dto.request;

import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportSearchDTO {
    private ReportStatus status;
    private ReportTargetType targetType;
} 