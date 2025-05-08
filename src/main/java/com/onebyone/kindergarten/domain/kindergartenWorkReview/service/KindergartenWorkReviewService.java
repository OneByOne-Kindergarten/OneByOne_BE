package com.onebyone.kindergarten.domain.kindergartenWorkReview.service;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.CreateWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReviewLikeHistory;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.exception.AlreadyExistWorkReviewException;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.exception.NotFoundWorkReviewException;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewLikeHistoryRepository;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KindergartenWorkReviewService {
    private final UserService userService;
    private final KindergartenService kindergartenService;
    private final KindergartenWorkReviewRepository workReviewRepository;
    private final KindergartenWorkReviewLikeHistoryRepository workReviewLikeHistoryRepository;
    private final NotificationTemplateService notificationTemplateService;

    @Transactional
    public Kindergarten createWorkReview(CreateWorkReviewRequestDTO request, String email) {
        User user = userService.getUserByEmail(email);
        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        boolean exists = workReviewRepository.existsByUserAndKindergarten(user, kindergarten);
        if (exists) {
            throw new AlreadyExistWorkReviewException("이미 등록된 근무 리뷰가 존재합니다.");
        }

        KindergartenWorkReview review = KindergartenWorkReview.builder()
                .user(user)
                .kindergarten(kindergarten)
                .workType(request.getWorkType())
                .workYear(request.getWorkYear())
                .oneLineComment(request.getOneLineComment())
                .benefitAndSalaryComment(request.getBenefitAndSalaryComment())
                .benefitAndSalaryScore(request.getBenefitAndSalaryScore())
                .workLifeBalanceComment(request.getWorkLifeBalanceComment())
                .workLifeBalanceScore(request.getWorkLifeBalanceScore())
                .workEnvironmentComment(request.getWorkEnvironmentComment())
                .workEnvironmentScore(request.getWorkEnvironmentScore())
                .managerComment(request.getManagerComment())
                .managerScore(request.getManagerScore())
                .customerComment(request.getCustomerComment())
                .customerScore(request.getCustomerScore())
                .status(ReviewStatus.ACCEPTED)
                .likeCount(0)
                .shareCount(0)
                .build();

        workReviewRepository.save(review);
        return kindergarten;
    }

    @Transactional
    public Kindergarten modifyWorkReview(ModifyWorkReviewRequestDTO request, String email) {
        User user = userService.getUserByEmail(email);
        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        KindergartenWorkReview review = workReviewRepository.findById(request.getWorkReviewId())
                .orElseThrow(() -> new NotFoundWorkReviewException("존재하지 않는 근무 리뷰입니다."));

        if (!review.getUser().equals(user)) {
            throw new SecurityException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.updateReview(request);
        return kindergarten;
    }

    @Transactional
    public void likeWorkReview(long reviewId, String email) {
        User user = userService.getUserByEmail(email);

        KindergartenWorkReview review = workReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundWorkReviewException("존재하지 않는 근무 리뷰입니다."));

        Optional<KindergartenWorkReviewLikeHistory> existingLike = workReviewLikeHistoryRepository.findByUserAndWorkReview(user, review);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            workReviewLikeHistoryRepository.delete(existingLike.get());
            review.minusLikeCount();
        } else {
            // 좋아요 추가
            KindergartenWorkReviewLikeHistory newLike = KindergartenWorkReviewLikeHistory.builder()
                    .user(user)
                    .workReview(review)
                    .build();

            workReviewLikeHistoryRepository.save(newLike);
            review.plusLikeCount();
            
            // 알림 발송 - 본인 글이 아닌 경우
            if (!review.getUser().getId().equals(user.getId())) {
                notificationTemplateService.sendLikeNotification(
                        review.getUser().getId(),
                        user,
                        review.getOneLineComment(),
                        review.getId()
                );
            }
        }

        workReviewRepository.save(review);
    }

    public WorkReviewPagedResponseDTO getReviews(Long kindergartenId, int page, int size, WorkReviewPagedResponseDTO.SortType sortType) {
        Pageable pageable;

        switch (sortType) {
            case POPULAR:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
                break;
            case LATEST:
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                break;
        }

        Page<WorkReviewDTO> reviewPage = workReviewRepository.findReviewsWithUserInfo(
            kindergartenId, 
            ReviewStatus.ACCEPTED, 
            pageable
        );

        return WorkReviewPagedResponseDTO.builder()
            .content(reviewPage.getContent())
            .totalPages(reviewPage.getTotalPages())
            .build();
    }
}