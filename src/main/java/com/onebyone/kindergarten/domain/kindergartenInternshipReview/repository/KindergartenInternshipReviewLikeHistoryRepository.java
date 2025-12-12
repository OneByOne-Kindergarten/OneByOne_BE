package com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReviewLikeHistory;
import com.onebyone.kindergarten.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindergartenInternshipReviewLikeHistoryRepository
    extends JpaRepository<KindergartenInternshipReviewLikeHistory, Long> {
  Optional<KindergartenInternshipReviewLikeHistory> findByUserAndInternshipReview(
      User user, KindergartenInternshipReview review);

  void deleteByUserAndInternshipReview(User user, KindergartenInternshipReview review);
}
