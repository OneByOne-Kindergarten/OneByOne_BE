package com.onebyone.kindergarten.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDTO {
    private boolean allNotificationsEnabled;
    private boolean communityNotificationsEnabled;
    private boolean eventNotificationsEnabled;
} 