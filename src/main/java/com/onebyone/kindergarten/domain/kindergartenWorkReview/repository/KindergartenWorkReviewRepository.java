package com.onebyone.kindergarten.domain.kindergartenWorkReview.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KindergartenWorkReviewRepository extends JpaRepository<KindergartenWorkReview, Long> {
    boolean existsByUserAndKindergarten(User user, Kindergarten kindergarten);

    List<KindergartenWorkReview> findByKindergartenAndStatus(Kindergarten kindergarten, ReviewStatus status);

    Page<KindergartenWorkReview> findByKindergartenIdAndStatus(Long kindergartenId, ReviewStatus reviewStatus, Pageable pageable);
}
