package com.onebyone.kindergarten.domain.kindergatens.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "kindergarten_work_review_aggregate")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KindergartenWorkReviewAggregate extends BaseEntity {
  @Id @GeneratedValue private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private Kindergarten kindergarten; // 유치원

  @Column(name = "benefit_and_salary_score_aggregate", precision = 10, scale = 2)
  private BigDecimal benefitAndSalaryScoreAggregate; // 복지/급여 총합

  @Column(name = "work_life_balance_score_aggregate", precision = 10, scale = 2)
  private BigDecimal workLiftBalanceScoreAggregate; // 워라벨 총합

  @Column(name = "work_environment_score_aggregate", precision = 10, scale = 2)
  private BigDecimal workEnvironmentScoreAggregate; // 분위기 총합

  @Column(name = "manager_score_aggregate", precision = 10, scale = 2)
  private BigDecimal managerScoreAggregate; // 관리자 총합

  @Column(name = "customer_score_aggregate", precision = 10, scale = 2)
  private BigDecimal customerScoreAggregate; // 고객 총합

  public void updateScoreAggregates(
      BigDecimal avgBenefitAndSalary,
      BigDecimal avgWorkLifeBalance,
      BigDecimal avgWorkEnvironment,
      BigDecimal avgManager,
      BigDecimal avgCustomer) {
    this.benefitAndSalaryScoreAggregate = avgBenefitAndSalary;
    this.workLiftBalanceScoreAggregate = avgWorkLifeBalance;
    this.workEnvironmentScoreAggregate = avgWorkEnvironment;
    this.managerScoreAggregate = avgManager;
    this.customerScoreAggregate = avgCustomer;
    this.updatedAt = LocalDateTime.now();
  }
}
