package com.onebyone.kindergarten.domain.kindergartenWorkReview.service;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.CreateWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReviewLikeHistory;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.enums.WorkReviewStarRatingType;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewLikeHistoryRepository;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
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
    private final KindergartenWorkReviewRepository kindergartenWorkReviewRepository;

    @Transactional
    public Kindergarten createWorkReview(CreateWorkReviewRequestDTO request, String email) {
        User user = userService.getUserByEmail(email);
        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        boolean exists = workReviewRepository.existsByUserAndKindergarten(user, kindergarten);
        if (exists) {
            throw new BusinessException(ErrorCodes.ALREADY_EXIST_WORK_REVIEW);
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
                .reviewStatus(ReviewStatus.ACCEPTED)
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
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_WORK_REVIEW));

        if (!review.getKindergarten().getId().equals(kindergarten.getId())) {
            throw new BusinessException(ErrorCodes.INCORRECT_KINDERGARTEN_EXCEPTION);
        }

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCodes.REVIEW_EDIT_NOT_OWNER);
        }

        review.updateReview(request);
        return kindergarten;
    }

    @Transactional
    public void likeWorkReview(long reviewId, String email) {
        User user = userService.getUserByEmail(email);

        KindergartenWorkReview review = workReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_WORK_REVIEW));

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

    public WorkReviewPagedResponseDTO getReviews(Long kindergartenId, int page, int size, WorkReviewPagedResponseDTO.SortType sortType, WorkReviewStarRatingType workReviewStarRatingType, int starRating) {
        if (workReviewStarRatingType != WorkReviewStarRatingType.ALL && starRating < 1 || starRating > 5) {
            throw new BusinessException(ErrorCodes.ILLEGAL_ARGUMENT_STAR_RATING_EXCEPTION);
        }

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

        Page<WorkReviewDTO> reviewPage;

        switch (workReviewStarRatingType) {
            case BENEFIT_AND_SALARY:
                reviewPage = kindergartenWorkReviewRepository
                        .findByBenefitAndSalaryScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case WORK_LIFE_BALANCE:
                reviewPage = kindergartenWorkReviewRepository
                        .findByWorkLifeBalanceScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case WORK_ENVIRONMENT:
                reviewPage = kindergartenWorkReviewRepository
                        .findByWorkEnvironmentScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case MANAGER:
                reviewPage = kindergartenWorkReviewRepository
                        .findByManagerScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case CUSTOMER:
                reviewPage = kindergartenWorkReviewRepository
                        .findByCustomerScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            default:
                reviewPage = workReviewRepository.findReviewsWithUserInfo(
                        kindergartenId,
                        ReviewStatus.ACCEPTED,
                        pageable
                );
                break;
        }

        return WorkReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 내가 작성한 근무 리뷰 조회
    public WorkReviewPagedResponseDTO getMyReviews(String email, int page, int size) {
        User user = userService.getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<WorkReviewDTO> reviewPage = workReviewRepository.findMyReviews(
                user.getId(),
                ReviewStatus.ACCEPTED,
                pageable
        );

        return WorkReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 전체 근무 리뷰 조회 (유치원 상관없이)
    public WorkReviewPagedResponseDTO getAllReviews(int page, int size, WorkReviewPagedResponseDTO.SortType sortType) {
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

        Page<WorkReviewDTO> reviewPage = workReviewRepository
                .findAllReviewsWithUserInfo(ReviewStatus.ACCEPTED, pageable);

        return WorkReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 근무 리뷰 삭제 (소프트 삭제)
    @Transactional
    public void deleteWorkReview(Long reviewId, String email) {
        // 리뷰 조회
        KindergartenWorkReview review = workReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_WORK_REVIEW));
        
        // 현재 사용자 조회
        User currentUser = userService.getUserByEmail(email);
        
        // 작성자 또는 관리자 권한 확인
        if (!review.getUser().getEmail().equals(email) && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }
        
        // 리뷰 소프트 삭제 (deletedAt 설정)
        review.markAsDeleted();
    }
}