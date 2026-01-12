package com.onebyone.kindergarten.domain.kindergartenWorkReview.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KindergartenWorkReviewRepository
    extends JpaRepository<KindergartenWorkReview, Long> {
  @Query(
      "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
          + "FROM kindergarten_work_review r "
          + "WHERE r.user = :user "
          + "AND r.kindergarten = :kindergarten "
          + "AND r.deletedAt IS NULL")
  boolean existsByUserAndKindergarten(
      @Param("user") User user, @Param("kindergarten") Kindergarten kindergarten);

  List<KindergartenWorkReview> findByKindergartenAndReviewStatus(
      Kindergarten kindergarten, ReviewStatus status);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findReviewsWithUserInfo(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      Pageable pageable);

  /// 내가 작성한 근무 리뷰 조회
  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.user.id = :userId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.deletedAt IS NULL "
          + "ORDER BY r.createdAt DESC")
  Page<WorkReviewDTO> findMyReviews(
      @Param("userId") Long userId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.benefitAndSalaryScore = :score "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findByBenefitAndSalaryScore(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      @Param("score") int score,
      Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.workLifeBalanceScore = :score "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findByWorkLifeBalanceScore(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      @Param("score") int score,
      Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.workEnvironmentScore = :score "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findByWorkEnvironmentScore(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      @Param("score") int score,
      Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.managerScore = :score "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findByManagerScore(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      @Param("score") int score,
      Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.kindergarten.id = :kindergartenId "
          + "AND r.reviewStatus = :reviewStatus "
          + "AND r.customerScore = :score "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findByCustomerScore(
      @Param("kindergartenId") Long kindergartenId,
      @Param("reviewStatus") ReviewStatus reviewStatus,
      @Param("score") int score,
      Pageable pageable);

  /// 전체 근무 리뷰 조회 (유치원 상관없이)
  @Query(
      "SELECT new com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO("
          + "r.id, u.id, u.nickname, k.id, k.name, r.workYear, r.oneLineComment, "
          + "r.benefitAndSalaryComment, r.benefitAndSalaryScore, "
          + "r.workLifeBalanceComment, r.workLifeBalanceScore, "
          + "r.workEnvironmentComment, r.workEnvironmentScore, "
          + "r.managerComment, r.managerScore, "
          + "r.customerComment, r.customerScore, "
          + "r.likeCount, r.shareCount, r.createdAt, r.workType) "
          + "FROM kindergarten_work_review r "
          + "JOIN r.user u "
          + "JOIN r.kindergarten k "
          + "WHERE r.reviewStatus = :reviewStatus "
          + "AND r.deletedAt IS NULL")
  Page<WorkReviewDTO> findAllReviewsWithUserInfo(
      @Param("reviewStatus") ReviewStatus reviewStatus, Pageable pageable);

  int countByUserIdAndReviewStatus(Long userId, ReviewStatus status);

  Integer countByUserId(Long userId);
}
