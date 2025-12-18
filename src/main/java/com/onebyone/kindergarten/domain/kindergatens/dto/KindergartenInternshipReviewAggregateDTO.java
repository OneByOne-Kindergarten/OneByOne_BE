package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenInternshipReviewAggregate;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KindergartenInternshipReviewAggregateDTO {

  private BigDecimal workEnvironmentScoreAggregate;
  private BigDecimal learningSupportScoreAggregate;
  private BigDecimal instructionTeacherScoreAggregate;

  public static KindergartenInternshipReviewAggregateDTO from(
      KindergartenInternshipReviewAggregate entity) {
    return KindergartenInternshipReviewAggregateDTO.builder()
        .workEnvironmentScoreAggregate(entity.getWorkEnvironmentScoreAggregate())
        .learningSupportScoreAggregate(entity.getLearningSupportScoreAggregate())
        .instructionTeacherScoreAggregate(entity.getInstructionTeacherScoreAggregate())
        .build();
  }
}
