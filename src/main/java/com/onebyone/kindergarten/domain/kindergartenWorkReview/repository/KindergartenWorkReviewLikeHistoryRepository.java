package com.onebyone.kindergarten.domain.kindergartenWorkReview.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReviewLikeHistory;
import com.onebyone.kindergarten.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindergartenWorkReviewLikeHistoryRepository
    extends JpaRepository<KindergartenWorkReviewLikeHistory, Long> {
  Optional<KindergartenWorkReviewLikeHistory> findByUserAndWorkReview(
      User user, KindergartenWorkReview review);

  void deleteByUserAndWorkReview(User user, KindergartenWorkReview review);
}
