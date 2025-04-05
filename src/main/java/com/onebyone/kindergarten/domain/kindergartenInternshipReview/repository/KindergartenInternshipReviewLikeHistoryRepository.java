package com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReviewLikeHistory;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KindergartenInternshipReviewLikeHistoryRepository extends JpaRepository<KindergartenInternshipReviewLikeHistory, Long> {
    Optional<KindergartenInternshipReviewLikeHistory> findByUserAndInternshipReview(User user, KindergartenInternshipReview review);

    void deleteByUserAndInternshipReview(User user, KindergartenInternshipReview review);
}
