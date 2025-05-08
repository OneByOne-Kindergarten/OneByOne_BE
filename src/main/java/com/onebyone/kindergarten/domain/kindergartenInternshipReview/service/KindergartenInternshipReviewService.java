package com.onebyone.kindergarten.domain.kindergartenInternshipReview.service;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.CreateInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.ModifyInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReviewLikeHistory;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception.AlreadyExistInternshipReviewException;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.exception.NotFoundInternshipReviewException;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewLikeHistoryRepository;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReviewStatus;
import com.onebyone.kindergarten.global.exception.IncorrectUserException;
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

    public Kindergarten createInternshipReview(CreateInternshipReviewRequestDTO request, String email) {
        User user = userService.getUserByEmail(email);

        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        boolean exists = kindergartenInternshipReviewRepository.existsByUserAndKindergarten(user, kindergarten);
        if (exists) {
            throw new AlreadyExistInternshipReviewException("이미 등록된 실습 리뷰가 존재합니다.");
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
                .status(ReviewStatus.ACCEPTED)
                .likeCount(0)
                .shareCount(0)
                .build();

        kindergartenInternshipReviewRepository.save(review);

        return kindergarten;
    }

    public Kindergarten modifyInternshipReview(ModifyInternshipReviewRequestDTO request, String email) {
        User user = userService.getUserByEmail(email);

        Kindergarten kindergarten = kindergartenService.getKindergartenById(request.getKindergartenId());

        KindergartenInternshipReview review = kindergartenInternshipReviewRepository
                .findById(request.getInternshipReviewId())
                .orElseThrow(() -> new NotFoundInternshipReviewException("존재하지 않는 실습 리뷰입니다."));

        if (!review.getUser().equals(user)) {
            throw new IncorrectUserException("작성자가 일치하지 않습니다.");
        }

        review.updateReview(request);

        return kindergarten;
    }

    @Transactional
    public void likeInternshipReview(long reviewId, String email) {
        User user = userService.getUserByEmail(email);

        KindergartenInternshipReview review = kindergartenInternshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundInternshipReviewException("존재하지 않는 실습 리뷰입니다."));

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

    public InternshipReviewPagedResponseDTO getReviews(Long kindergartenId, int page, int size, InternshipReviewPagedResponseDTO.SortType sortType) {
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

        Page<InternshipReviewDTO> reviewPage = kindergartenInternshipReviewRepository.findReviewsWithUserInfo(
            kindergartenId, 
            ReviewStatus.ACCEPTED, 
            pageable
        );

        return InternshipReviewPagedResponseDTO.builder()
            .content(reviewPage.getContent())
            .totalPages(reviewPage.getTotalPages())
            .build();
    }
}
