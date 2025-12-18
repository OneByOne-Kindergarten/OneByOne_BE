package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenWorkReviewAggregate;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KindergartenWorkReviewAggregateDTO {
  private BigDecimal benefitAndSalaryScoreAggregate;
  private BigDecimal workLiftBalanceScoreAggregate;
  private BigDecimal workEnvironmentScoreAggregate;
  private BigDecimal managerScoreAggregate;
  private BigDecimal customerScoreAggregate;

  public static KindergartenWorkReviewAggregateDTO from(KindergartenWorkReviewAggregate entity) {
    return KindergartenWorkReviewAggregateDTO.builder()
        .benefitAndSalaryScoreAggregate(entity.getBenefitAndSalaryScoreAggregate())
        .workLiftBalanceScoreAggregate(entity.getWorkLiftBalanceScoreAggregate())
        .workEnvironmentScoreAggregate(entity.getWorkEnvironmentScoreAggregate())
        .managerScoreAggregate(entity.getManagerScoreAggregate())
        .customerScoreAggregate(entity.getCustomerScoreAggregate())
        .build();
  }
}
