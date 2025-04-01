package com.onebyone.kindergarten.domain.kindergartenWorkReviewAggregate.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "kindergarten_work_review_aggregate")
@Getter
public class KindergartenWorkReviewAggregate extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Kindergarten kindergarten; // 유치원

    @Column(name = "benefit_and_salary_score_aggregate")
    private Double benefitAndSalaryScoreAggregate; // 복지/급여 총합

    @Column(name = "work_life_balance_score_aggregate")
    private Double workLiftBalanceScoreAggregate; // 워라벨 총합

    @Column(name = "work_envoronment_score_aggregate")
    private Double workEnvironmentScoreAggregate; // 분위기 총합

    @Column(name = "manager_score_aggregate")
    private Double managerScoreAggregate; // 관리자 총합

    @Column(name = "customer_score_aggregate")
    private Double customerScoreAggregate; // 고객 총합
}
