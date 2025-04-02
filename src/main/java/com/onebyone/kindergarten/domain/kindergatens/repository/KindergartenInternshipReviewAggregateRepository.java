package com.onebyone.kindergarten.domain.kindergatens.repository;

import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenInternshipReviewAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindergartenInternshipReviewAggregateRepository extends JpaRepository<KindergartenInternshipReviewAggregate, Long> {

}
