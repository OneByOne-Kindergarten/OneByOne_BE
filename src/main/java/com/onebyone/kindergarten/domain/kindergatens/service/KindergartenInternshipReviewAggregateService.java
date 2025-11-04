package com.onebyone.kindergarten.domain.kindergatens.service;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenInternshipReviewAggregate;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenInternshipReviewAggregateRepository;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KindergartenInternshipReviewAggregateService {
    private final KindergartenInternshipReviewRepository kindergartenInternshipReviewRepository;
    private final KindergartenInternshipReviewAggregateRepository kindergartenInternshipReviewAggregateRepository;

    public void updateOrCreateAggregate(Kindergarten kindergarten) {
        List<KindergartenInternshipReview> acceptedReviews = kindergartenInternshipReviewRepository.findByKindergartenAndReviewStatus(kindergarten, ReviewStatus.ACCEPTED);

        if (acceptedReviews.isEmpty()) {
            return;
        }

        int totalWorkEnvironmentScore = 0;
        int totalLearningSupportScore = 0;
        int totalInstructionTeacherScore = 0;

        for (KindergartenInternshipReview review : acceptedReviews) {
            totalWorkEnvironmentScore += review.getWorkEnvironmentScore();
            totalLearningSupportScore += review.getLearningSupportScore();
            totalInstructionTeacherScore += review.getInstructionTeacherScore();
        }

        int reviewCount = acceptedReviews.size();

        BigDecimal avgWorkEnvironmentScore = BigDecimal.valueOf((double) totalWorkEnvironmentScore / reviewCount);
        BigDecimal avgLearningSupportScore = BigDecimal.valueOf((double) totalLearningSupportScore / reviewCount);
        BigDecimal avgInstructionTeacherScore = BigDecimal.valueOf((double) totalInstructionTeacherScore / reviewCount);

        KindergartenInternshipReviewAggregate aggregate = kindergartenInternshipReviewAggregateRepository.findByKindergarten(kindergarten);

        aggregate.updateScoreAggregates(avgWorkEnvironmentScore, avgLearningSupportScore, avgInstructionTeacherScore);
    }
}
