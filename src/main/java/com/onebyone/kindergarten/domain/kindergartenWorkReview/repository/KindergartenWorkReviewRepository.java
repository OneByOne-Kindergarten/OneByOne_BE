package com.onebyone.kindergarten.domain.kindergartenWorkReview.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KindergartenWorkReviewRepository extends JpaRepository<KindergartenWorkReview, Long> {
    boolean existsByUserAndKindergarten(User user, Kindergarten kindergarten);

    List<KindergartenWorkReview> findByKindergartenAndStatus(Kindergarten kindergarten, ReviewStatus status);

    @Query("SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO(" +
           "r.id, u.id, u.nickname, r.workYear, r.oneLineComment, " +
           "r.benefitAndSalaryComment, r.benefitAndSalaryScore, " +
           "r.workLifeBalanceComment, r.workLifeBalanceScore, " +
           "r.workEnvironmentComment, r.workEnvironmentScore, " +
           "r.managerComment, r.managerScore, " +
           "r.customerComment, r.customerScore, " +
           "r.likeCount, r.shareCount, r.createdAt, r.workType) " +
           "FROM kindergarten_work_review r " +
           "JOIN r.user u " +
           "WHERE r.kindergarten.id = :kindergartenId " +
           "AND r.status = :reviewStatus")
    Page<WorkReviewDTO> findReviewsWithUserInfo(
        @Param("kindergartenId") Long kindergartenId,
        @Param("reviewStatus") ReviewStatus reviewStatus,
        Pageable pageable
    );
}
