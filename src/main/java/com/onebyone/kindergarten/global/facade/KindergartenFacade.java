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
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
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
    public void createInternshipReview(CreateInternshipReviewRequestDTO request, Long userId) {
        Kindergarten kindergarten = kindergartenInternshipReviewService.createInternshipReview(request, userId);

        kindergartenInternshipReviewAggregateService.updateOrCreateAggregate(kindergarten);
        
        /// 사용자 리뷰 작성 플래그 업데이트
        userService.markUserAsReviewWriter(userId);
    }

    @Transactional
    public void modifyInternshipReview(ModifyInternshipReviewRequestDTO request, Long userId) {
        Kindergarten kindergarten = kindergartenInternshipReviewService.modifyInternshipReview(request, userId);

        kindergartenInternshipReviewAggregateService.updateOrCreateAggregate(kindergarten);
    }

    @Transactional
    public void createWorkReview(CreateWorkReviewRequestDTO request, Long userId) {
        Kindergarten kindergarten = kindergartenWorkReviewService.createWorkReview(request, userId);

        kindergartenWorkReviewAggregateService.updateOrCreateAggregate(kindergarten);
        
        /// 사용자 리뷰 작성 플래그 업데이트
        userService.markUserAsReviewWriter(userId);
    }

    @Transactional
    public void modifyWorkReview(ModifyWorkReviewRequestDTO request, Long userId) {
        Kindergarten kindergarten = kindergartenWorkReviewService.modifyWorkReview(request, userId);

        kindergartenWorkReviewAggregateService.updateOrCreateAggregate(kindergarten);
    }

    @Transactional
    public void deleteWorkReview(Long reviewId, Long userId) {
        User currentUser = userService.getUserById(userId);
        kindergartenWorkReviewService.deleteWorkReview(reviewId, currentUser.getId(), currentUser.getRole());
        int workReviewCount = kindergartenWorkReviewService.countReviewsByUser(currentUser.getId(), ReviewStatus.ACCEPTED);
        int internshipReviewCount = kindergartenInternshipReviewService.countReviewsByUser(currentUser.getId(), ReviewStatus.ACCEPTED);
        if (workReviewCount + internshipReviewCount == 0) {
            currentUser.unMarkAsReviewWriter();
        }
    }

    @Transactional
    public void deleteInternshipReview(Long reviewId, Long userId) {
        User currentUser = userService.getUserById(userId);
        kindergartenInternshipReviewService.deleteWorkReview(reviewId, currentUser.getId(), currentUser.getRole());
        int workReviewCount = kindergartenWorkReviewService.countReviewsByUser(currentUser.getId(), ReviewStatus.ACCEPTED);
        int internshipReviewCount = kindergartenInternshipReviewService.countReviewsByUser(currentUser.getId(), ReviewStatus.ACCEPTED);
        if (workReviewCount + internshipReviewCount == 0) {
            currentUser.unMarkAsReviewWriter();
        }
    }
}

