package com.onebyone.kindergarten.domain.kindergatens.repository;

import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenWorkReviewAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindergartenWorkReviewAggregateRepository extends JpaRepository<KindergartenWorkReviewAggregate, Long> {
}
