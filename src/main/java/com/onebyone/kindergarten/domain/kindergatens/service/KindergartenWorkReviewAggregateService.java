package com.onebyone.kindergarten.domain.kindergatens.service;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenWorkReviewAggregate;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenWorkReviewAggregateRepository;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KindergartenWorkReviewAggregateService {
    private final KindergartenWorkReviewRepository workReviewRepository;
    private final KindergartenWorkReviewAggregateRepository workReviewAggregateRepository;

    public void updateOrCreateAggregate(Kindergarten kindergarten) {
        List<KindergartenWorkReview> acceptedReviews = workReviewRepository.findByKindergartenAndStatus(kindergarten, ReviewStatus.ACCEPTED);

        if (acceptedReviews.isEmpty()) {
            return;
        }

        int totalBenefitAndSalary = 0;
        int totalWorkLifeBalance = 0;
        int totalWorkEnvironment = 0;
        int totalManager = 0;
        int totalCustomer = 0;

        for (KindergartenWorkReview review : acceptedReviews) {
            totalBenefitAndSalary += review.getBenefitAndSalaryScore();
            totalWorkLifeBalance += review.getWorkLifeBalanceScore();
            totalWorkEnvironment += review.getWorkEnvironmentScore();
            totalManager += review.getManagerScore();
            totalCustomer += review.getCustomerScore();
        }

        int reviewCount = acceptedReviews.size();

        BigDecimal avgBenefitAndSalary = BigDecimal.valueOf((double) totalBenefitAndSalary / reviewCount);
        BigDecimal avgWorkLifeBalance = BigDecimal.valueOf((double) totalWorkLifeBalance / reviewCount);
        BigDecimal avgWorkEnvironment = BigDecimal.valueOf((double) totalWorkEnvironment / reviewCount);
        BigDecimal avgManager = BigDecimal.valueOf((double) totalManager / reviewCount);
        BigDecimal avgCustomer = BigDecimal.valueOf((double) totalCustomer / reviewCount);

        KindergartenWorkReviewAggregate aggregate = workReviewAggregateRepository.findByKindergarten(kindergarten);
        aggregate.updateScoreAggregates(avgBenefitAndSalary, avgWorkLifeBalance, avgWorkEnvironment, avgManager, avgCustomer);
    }
}
