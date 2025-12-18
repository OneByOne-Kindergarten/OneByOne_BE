package com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewType;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KindergartenWorkHistoryRequest {
  private String kindergartenName;
  private LocalDate startDate;
  private LocalDate endDate;
  private ReviewType workType;

  public KindergartenWorkHistory toEntity(User user, Kindergarten kindergarten) {
    return KindergartenWorkHistory.builder()
        .user(user)
        .kindergarten(kindergarten)
        .startDate(startDate)
        .endDate(endDate)
        .workType(workType)
        .build();
  }
}
