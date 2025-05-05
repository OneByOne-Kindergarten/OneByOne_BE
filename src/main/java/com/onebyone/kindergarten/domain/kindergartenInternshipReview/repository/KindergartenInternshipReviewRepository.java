package com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KindergartenInternshipReviewRepository extends JpaRepository<KindergartenInternshipReview, Long> {
    boolean existsByUserAndKindergarten(User user, Kindergarten kindergarten);

    List<KindergartenInternshipReview> findByKindergartenAndStatus(Kindergarten kindergarten, ReviewStatus status);

    Page<KindergartenInternshipReview> findByKindergartenIdAndStatus(Long kindergartenId, ReviewStatus reviewStatus, Pageable pageable);
}
