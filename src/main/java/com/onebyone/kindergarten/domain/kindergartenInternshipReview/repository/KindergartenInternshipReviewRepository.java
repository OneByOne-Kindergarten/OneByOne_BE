package com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KindergartenInternshipReviewRepository extends JpaRepository<KindergartenInternshipReview, Long> {
    boolean existsByUserAndKindergarten(User user, Kindergarten kindergarten);

    List<KindergartenInternshipReview> findByKindergartenAndStatus(Kindergarten kindergarten, ReviewStatus status);

    @Query("SELECT new com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewDTO(" +
           "r.id, u.id, u.nickname, k.id, k.name, r.oneLineComment, " +
           "r.workEnvironmentComment, r.workEnvironmentScore, " +
           "r.learningSupportComment, r.learningSupportScore, " +
           "r.instructionTeacherComment, r.instructionTeacherScore, " +
           "r.likeCount, r.shareCount, r.createdAt) " +
           "FROM kindergarten_internship_review r " +
           "JOIN r.user u " +
           "JOIN r.kindergarten k " +
           "WHERE r.kindergarten.id = :kindergartenId " +
           "AND r.status = :reviewStatus " +
           "AND r.deletedAt IS NULL")
    Page<InternshipReviewDTO> findReviewsWithUserInfo(
        @Param("kindergartenId") Long kindergartenId,
        @Param("reviewStatus") ReviewStatus reviewStatus,
        Pageable pageable
    );

    /// 내가 작성한 실습 리뷰 조회
    @Query("SELECT new com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewDTO(" +
           "r.id, u.id, u.nickname, k.id, k.name, r.oneLineComment, " +
           "r.workEnvironmentComment, r.workEnvironmentScore, " +
           "r.learningSupportComment, r.learningSupportScore, " +
           "r.instructionTeacherComment, r.instructionTeacherScore, " +
           "r.likeCount, r.shareCount, r.createdAt) " +
           "FROM kindergarten_internship_review r " +
           "JOIN r.user u " +
           "JOIN r.kindergarten k " +
           "WHERE r.user.id = :userId " +
           "AND r.status = :reviewStatus " +
           "AND r.deletedAt IS NULL " +
           "ORDER BY r.createdAt DESC")
    Page<InternshipReviewDTO> findMyReviews(
        @Param("userId") Long userId,
        @Param("reviewStatus") ReviewStatus reviewStatus,
        Pageable pageable
    );
}
