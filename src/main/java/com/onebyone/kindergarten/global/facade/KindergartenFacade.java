package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.CreateInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.ModifyInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.service.KindergartenInternshipReviewService;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.CreateWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.service.KindergartenWorkReviewService;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenInternshipReviewAggregateService;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenWorkReviewAggregateService;
import com.onebyone.kindergarten.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KindergartenFacade {
    private final KindergartenInternshipReviewService kindergartenInternshipReviewService;
    private final KindergartenInternshipReviewAggregateService kindergartenInternshipReviewAggregateService;
    private final KindergartenWorkReviewService kindergartenWorkReviewService;
    private final KindergartenWorkReviewAggregateService kindergartenWorkReviewAggregateService;
    private final UserService userService;


    @Transactional
    public void createInternshipReview(CreateInternshipReviewRequestDTO request, String email) {
        Kindergarten kindergarten = kindergartenInternshipReviewService.createInternshipReview(request, email);

        kindergartenInternshipReviewAggregateService.updateOrCreateAggregate(kindergarten);
        
        /// 사용자 리뷰 작성 플래그 업데이트
        userService.markUserAsReviewWriter(email);
    }

    @Transactional
    public void modifyInternshipReview(ModifyInternshipReviewRequestDTO request, String email) {
        Kindergarten kindergarten = kindergartenInternshipReviewService.modifyInternshipReview(request, email);

        kindergartenInternshipReviewAggregateService.updateOrCreateAggregate(kindergarten);
    }

    @Transactional
    public void createWorkReview(CreateWorkReviewRequestDTO request, String email) {
        Kindergarten kindergarten = kindergartenWorkReviewService.createWorkReview(request, email);

        kindergartenWorkReviewAggregateService.updateOrCreateAggregate(kindergarten);
        
        /// 사용자 리뷰 작성 플래그 업데이트
        userService.markUserAsReviewWriter(email);
    }

    @Transactional
    public void modifyWorkReview(ModifyWorkReviewRequestDTO request, String email) {
        Kindergarten kindergarten = kindergartenWorkReviewService.modifyWorkReview(request, email);

        kindergartenWorkReviewAggregateService.updateOrCreateAggregate(kindergarten);
    }
}

