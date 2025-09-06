package com.onebyone.kindergarten.domain.kindergartenWorkReview.entity;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "kindergarten_work_review")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KindergartenWorkReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 리뷰 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten", nullable = false)
    private Kindergarten kindergarten; // 유치원

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.ACCEPTED;

    @Column(name = "work_type", nullable = false, columnDefinition = "varchar(255) default ''")
    private String workType; // 근무 형태

    @Column(name = "work_year", nullable = false)
    private Integer workYear; // 근무/실습 년수

    @Column(name = "one_line_comment", nullable = false)
    private String oneLineComment; // 한 줄 평가

    @Column(name = "benefit_and_salary_comment", nullable = false)
    private String benefitAndSalaryComment; // 복지/급여 평가

    @Column(name = "benefit_and_salary_score", nullable = false)
    private Integer benefitAndSalaryScore; // 복지/급여 점수

    @Column(name = "work_life_balance_comment", nullable = false)
    private String workLifeBalanceComment; // 워라벨 평가

    @Column(name = "work_life_balance_score", nullable = false)
    private Integer workLifeBalanceScore; // 워라벨 점수

    @Column(name = "work_environment_comment", nullable = false)
    private String workEnvironmentComment; // 분위기 평가

    @Column(name = "work_environment_score", nullable = false)
    private Integer workEnvironmentScore; // 분위기 점수

    @Column(name = "manager_comment", nullable = false)
    private String managerComment; // 관리자 평가

    @Column(name = "manager_score", nullable = false)
    private Integer managerScore; // 관리자 점수

    @Column(name = "customer_comment", nullable = false)
    private String customerComment; // 고객 평가

    @Column(name = "customer_score", nullable = false)
    private Integer customerScore; // 고객 점수

    @Column(name = "like_count")
    private Integer likeCount = 0; // 좋아요 수

    @Column(name = "share_count")
    private Integer shareCount = 0; // 공유 수

    public void updateReview(ModifyWorkReviewRequestDTO request) {
        this.oneLineComment = request.getOneLineComment();
        this.workType = request.getWorkType();
        this.benefitAndSalaryComment = request.getBenefitAndSalaryComment();
        this.benefitAndSalaryScore = request.getBenefitAndSalaryScore();
        this.workLifeBalanceComment = request.getWorkLifeBalanceComment();
        this.workLifeBalanceScore = request.getWorkLifeBalanceScore();
        this.workEnvironmentComment = request.getWorkEnvironmentComment();
        this.workEnvironmentScore = request.getWorkEnvironmentScore();
        this.managerComment = request.getManagerComment();
        this.managerScore = request.getManagerScore();
        this.customerComment = request.getCustomerComment();
        this.customerScore = request.getCustomerScore();
    }

    public void minusLikeCount() {
        this.likeCount--;
    }

    public void plusLikeCount() {
        this.likeCount++;
    }

    /// 리뷰 소프트 삭제
    public void markAsDeleted() {
        this.deletedAt = java.time.LocalDateTime.now();
    }
}