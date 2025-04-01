package com.onebyone.kindergarten.domain.kindergartenInternshipReviewAggregate.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "kindergarten_internship_review_aggregate")
@Getter
public class KindergartenInternshipReviewAggregate extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Kindergarten kindergarten; // 유치원

    @Column(name = "work_envoronment_score_aggregate")
    private Double workEnvironmentScoreAggregate; // 분위기 총합

    @Column(name = "learning_support_score_aggregate")
    private Double learningSupportScoreAggregate; // 학습 총합

    @Column(name = "instruction_teacher_score_aggregate")
    private Double instructionTeacherScoreAggregate; // 지도교사 총합
}
