package com.onebyone.kindergarten.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardDTO {
    private final long pendingReports;
    private final long pendingInquiries;
} 