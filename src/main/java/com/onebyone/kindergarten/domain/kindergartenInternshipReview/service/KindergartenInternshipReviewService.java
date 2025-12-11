package com.onebyone.kindergarten.domain.kindergartenInternshipReview.service;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.CreateInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.ModifyInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReviewLikeHistory;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.enums.InternshipReviewStarRatingType;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewLikeHistoryRepository;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
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
public class KindergartenInternshipReviewService {
    private final UserService userService;
    private final KindergartenService kindergartenService;
    private final KindergartenInternshipReviewRepository kindergartenInternshipReviewRepository;
    private final KindergartenInternshipReviewLikeHistoryRepository kindergartenInternshipReviewLikeHistoryRepository;

    public Kindergarten createInternshipReview(CreateInternshipReviewRequestDTO request, Long userId) {
        User user = userService.getUserById(userId);

        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        boolean exists = kindergartenInternshipReviewRepository.existsByUserAndKindergarten(user, kindergarten);
        if (exists) {
            throw new BusinessException(ErrorCodes.ALREADY_EXIST_INTERNSHIP_REVIEW);
        }

        KindergartenInternshipReview review = KindergartenInternshipReview.builder()
                .user(user)
                .kindergarten(kindergarten)
                .workType(request.getWorkType())
                .oneLineComment(request.getOneLineComment())
                .workEnvironmentComment(request.getWorkEnvironmentComment())
                .workEnvironmentScore(request.getWorkEnvironmentScore())
                .learningSupportComment(request.getLearningSupportComment())
                .learningSupportScore(request.getLearningSupportScore())
                .instructionTeacherComment(request.getInstructionTeacherComment())
                .instructionTeacherScore(request.getInstructionTeacherScore())
                .reviewStatus(ReviewStatus.ACCEPTED)
                .likeCount(0)
                .shareCount(0)
                .build();

        kindergartenInternshipReviewRepository.save(review);

        return kindergarten;
    }

    public Kindergarten modifyInternshipReview(ModifyInternshipReviewRequestDTO request, Long userId) {
        User user = userService.getUserById(userId);
        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        KindergartenInternshipReview review = kindergartenInternshipReviewRepository
                .findById(request.getInternshipReviewId())
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_INTERNSHIP_REVIEW));

        // 리뷰와 유치원이 다를 때
        if (!review.getKindergarten().getId().equals(kindergarten.getId())) {
            throw new BusinessException(ErrorCodes.INCORRECT_KINDERGARTEN_EXCEPTION);
        }

        // 리뷰 작성자가 다를 때
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCodes.REVIEW_EDIT_NOT_OWNER);
        }

        review.updateReview(request);
        return kindergarten;
    }

    @Transactional
    public void likeInternshipReview(long reviewId, Long userId) {
        User user = userService.getUserById(userId);

        KindergartenInternshipReview review = kindergartenInternshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_INTERNSHIP_REVIEW));

        Optional<KindergartenInternshipReviewLikeHistory> existingLike = kindergartenInternshipReviewLikeHistoryRepository.findByUserAndInternshipReview(user, review);

        if (existingLike.isPresent()) {
            kindergartenInternshipReviewLikeHistoryRepository.delete(existingLike.get());
            review.minusLikeCount();
        } else {
            KindergartenInternshipReviewLikeHistory newLike = KindergartenInternshipReviewLikeHistory.builder()
                    .user(user)
                    .internshipReview(review)
                    .build();

            kindergartenInternshipReviewLikeHistoryRepository.save(newLike);
            review.plusLikeCount();
        }

        kindergartenInternshipReviewRepository.save(review);
    }

    public InternshipReviewPagedResponseDTO getReviews(Long kindergartenId, int page, int size, InternshipReviewPagedResponseDTO.SortType sortType, InternshipReviewStarRatingType internshipReviewStarRatingType, int starRating) {
        if (internshipReviewStarRatingType != InternshipReviewStarRatingType.ALL && starRating < 1 || starRating > 5) {
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

        Page<InternshipReviewDTO> reviewPage;

        switch (internshipReviewStarRatingType) {
            case WORK_ENVIRONMENT:
                reviewPage = kindergartenInternshipReviewRepository
                        .findByWorkEnvironmentScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case LEARNING_SUPPORT:
                reviewPage = kindergartenInternshipReviewRepository
                        .findByLearningSupportScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case INSTRUCTION_TEACHER:
                reviewPage = kindergartenInternshipReviewRepository
                        .findByInstructionTeacherScore(
                                kindergartenId, ReviewStatus.ACCEPTED, starRating, pageable);
                break;
            case ALL:
            default:
                reviewPage = kindergartenInternshipReviewRepository
                        .findReviewsWithUserInfo(
                                kindergartenId, ReviewStatus.ACCEPTED, pageable);
                break;
        }

        return InternshipReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 내가 작성한 실습 리뷰 조회
    public InternshipReviewPagedResponseDTO getMyReviews(Long userId, int page, int size) {
        User user = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<InternshipReviewDTO> reviewPage = kindergartenInternshipReviewRepository.findMyReviews(
                user.getId(),
                ReviewStatus.ACCEPTED,
                pageable
        );

        return InternshipReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 전체 실습 리뷰 조회 (유치원 상관없이)
    public InternshipReviewPagedResponseDTO getAllReviews(int page, int size, InternshipReviewPagedResponseDTO.SortType sortType) {
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

        Page<InternshipReviewDTO> reviewPage = kindergartenInternshipReviewRepository
                .findAllReviewsWithUserInfo(ReviewStatus.ACCEPTED, pageable);

        return InternshipReviewPagedResponseDTO.builder()
                .content(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    /// 실습 리뷰 삭제 (소프트 삭제)
    @Transactional
    public void deleteInternshipReview(Long reviewId, String email) {
        // 리뷰 조회
        KindergartenInternshipReview review = kindergartenInternshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_INTERNSHIP_REVIEW));
        
        // 현재 사용자 조회
        User currentUser = userService.getUserByEmail(email);
        
        // 작성자 또는 관리자 권한 확인
        if (!review.getUser().getEmail().equals(email) && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }
        
        // 리뷰 소프트 삭제 (deletedAt 설정)
        review.markAsDeleted();
    }

    public int countReviewsByUser(Long userId, ReviewStatus reviewStatus) {
        return kindergartenInternshipReviewRepository.countByUserIdAndReviewStatus(userId, reviewStatus);
    }

    public void deleteWorkReview(Long reviewId, Long userId, UserRole role) {
        // 리뷰 조회
        KindergartenInternshipReview review = kindergartenInternshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_INTERNSHIP_REVIEW));

        // 작성자 또는 관리자 권한 확인
        if (!review.getUser().getId().equals(userId) && !role.equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }

        // 리뷰 소프트 삭제 (deletedAt 설정)
        review.markAsDeleted();
    }
}
