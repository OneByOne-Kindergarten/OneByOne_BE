package com.onebyone.kindergarten.domain.kindergatens.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "kindergarten_internship_review_aggregate")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KindergartenInternshipReviewAggregate extends BaseEntity {
  @Id @GeneratedValue private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private Kindergarten kindergarten; // 유치원

  @Column(name = "work_environment_score_aggregate", precision = 10, scale = 2)
  private BigDecimal workEnvironmentScoreAggregate; // 분위기 총합

  @Column(name = "learning_support_score_aggregate", precision = 10, scale = 2)
  private BigDecimal learningSupportScoreAggregate; // 학습 총합

  @Column(name = "instruction_teacher_score_aggregate", precision = 10, scale = 2)
  private BigDecimal instructionTeacherScoreAggregate; // 지도교사 총합

  public void updateScoreAggregates(
      BigDecimal avgWorkEnvironmentScore,
      BigDecimal avgLearningSupportScore,
      BigDecimal avgInstructionTeacherScore) {
    this.workEnvironmentScoreAggregate = avgWorkEnvironmentScore;
    this.learningSupportScoreAggregate = avgLearningSupportScore;
    this.instructionTeacherScoreAggregate = avgInstructionTeacherScore;
    this.updatedAt = LocalDateTime.now();
  }
}
