package com.onebyone.kindergarten.domain.kindergatens.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity(name = "kindergarten_internship_review_aggregate")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KindergartenInternshipReviewAggregate extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Kindergarten kindergarten; // 유치원

    @Column(name = "work_environment_score_aggregate", precision = 10, scale = 2)
    private BigDecimal workEnvironmentScoreAggregate; // 분위기 총합

    @Column(name = "learning_support_score_aggregate", precision = 10, scale = 2)
    private BigDecimal learningSupportScoreAggregate; // 학습 총합

    @Column(name = "instruction_teacher_score_aggregate", precision = 10, scale = 2)
    private BigDecimal instructionTeacherScoreAggregate; // 지도교사 총합

    public void updateWorkEnvironmentScoreAggregate(BigDecimal avgWorkEnvironmentScore) {
        this.workEnvironmentScoreAggregate = avgWorkEnvironmentScore;
    }

    public void updateLearningSupportScoreAggregate(BigDecimal avgLearningSupportScore) {
        this.learningSupportScoreAggregate = avgLearningSupportScore;
    }

    public void updateInstructionTeacherScoreAggregate(BigDecimal avgInstructionTeacherScore) {
        this.instructionTeacherScoreAggregate = avgInstructionTeacherScore;
    }
}
