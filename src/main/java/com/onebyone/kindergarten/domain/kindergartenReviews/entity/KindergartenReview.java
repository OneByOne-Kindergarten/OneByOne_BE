package com.onebyone.kindergarten.domain.kindergartenReviews.entity;

import com.onebyone.kindergarten.domain.kindergartenReviews.dto.CategoryDetail;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReviewType;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;
import java.util.HashMap;

@Entity(name = "kindergarten_review")
@Getter
public class KindergartenReview extends BaseEntity {
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
    @Column(name = "review_type")
    private ReviewType reviewType; // 리뷰 타입 - 근무, 실습

    @Column(name = "review_score", nullable = false)
    private Double reviewScore; // 리뷰 점수 - 실수

    @Column(name = "work_year", nullable = false)
    private Integer workYear; // 근무/실습 년수

    @Column(nullable = false)
    private Double rating; // 평점

    @Column(name = "like_count")
    private Integer likeCount = 0; // 좋아요 수

    @Column(name = "share_count")
    private Integer shareCount = 0; // 공유 수

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.YET; // 게시글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, CategoryDetail> categoryScores; // 카테고리별 점수와 내용

    public void setCategoryScores(Map<String, CategoryDetail> categoryScores) {
        this.categoryScores = categoryScores;
    }

    // 카테고리 추가 메서드
    public void addCategory(String categoryName, CategoryDetail detail) {
        if (this.categoryScores == null) {
            this.categoryScores = new HashMap<>();
        }
        this.categoryScores.put(categoryName, detail);
    }

    // 카테고리 제거 메서드
    public void removeCategory(String categoryName) {
        if (this.categoryScores != null) {
            this.categoryScores.remove(categoryName);
        }
    }
}