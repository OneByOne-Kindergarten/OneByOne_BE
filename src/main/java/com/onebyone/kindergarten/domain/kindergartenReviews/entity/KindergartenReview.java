package com.onebyone.kindergarten.domain.kindergartenReviews.entity;

import com.onebyone.kindergarten.global.enums.ReviewType;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class KindergartenReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 리뷰 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id", nullable = false)
    private Kindergarten kindergarten; // 유치원

    @Column(nullable = false, length = 1000)
    private String content; // 리뷰 내용

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType; // 리뷰 타입 - 근무, 실습

    @Column(nullable = false)
    private Double reviewScore; // 리뷰 점수 - 실수

    @Column(nullable = false)
    private Integer workYear; // 근무/실습 년수

    @Column(nullable = false)
    private Double rating; // 평점

    private Integer likeCount = 0; // 좋아요 수
    private Integer shareCount = 0; // 공유 수

    private ReportStatus status = ReportStatus.YET; // 게시글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)

    private LocalDateTime deletedAt; // 삭제일

    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 수정일
}