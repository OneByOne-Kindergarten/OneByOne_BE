package com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "kindergarten_internship_review")
@Getter
public class KindergartenInternshipReview extends BaseEntity {
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
    private ReportStatus status = ReportStatus.YET; // 게시글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)

    @Column(name = "one_line_comment", nullable = false)
    private String oneLineComment; // 한 줄 평가

    @Column(name = "work_envoronment_comment", nullable = false)
    private String workEnvironmentComment; // 분위기 평가

    @Column(name = "work_envoronment_score", nullable = false)
    private Integer workEnvironmentScore; // 분위기 점수

    @Column(name = "learning_support_comment", nullable = false)
    private String learningSupportComment; // 학습 도움 평가

    @Column(name = "learning_support_score", nullable = false)
    private Integer learningSupportScore; // 학습 도움 점수

    @Column(name = "instruction_teacher_comment", nullable = false)
    private String instructionTeacherComment; // 지도 교사 평가

    @Column(name = "instruction_teacher_score", nullable = false)
    private Integer instructionTeacherScore; // 지도 교사 점수

    @Column(name = "like_count")
    private Integer likeCount = 0; // 좋아요 수

    @Column(name = "share_count")
    private Integer shareCount = 0; // 공유 수
}