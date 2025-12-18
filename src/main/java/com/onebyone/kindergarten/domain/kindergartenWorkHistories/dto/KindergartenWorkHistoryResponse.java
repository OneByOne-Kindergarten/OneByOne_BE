package com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.global.enums.ReviewType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KindergartenWorkHistoryResponse {
  private Long id;
  private String kindergartenName;
  private LocalDate startDate;
  private LocalDate endDate;
  private ReviewType workType;

  public static KindergartenWorkHistoryResponse from(KindergartenWorkHistory history) {
    return KindergartenWorkHistoryResponse.builder()
        .id(history.getId())
        .kindergartenName(history.getKindergarten().getName())
        .startDate(history.getStartDate())
        .endDate(history.getEndDate())
        .workType(history.getWorkType())
        .build();
  }
}
