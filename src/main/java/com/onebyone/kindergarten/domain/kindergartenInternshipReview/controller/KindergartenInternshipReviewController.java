package com.onebyone.kindergarten.domain.kindergartenInternshipReview.controller;

import com.onebyone.kindergarten.domain.facade.KindergartenFacade;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.CreateInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.ModifyInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.enums.InternshipReviewStarRatingType;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.service.KindergartenInternshipReviewService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유치원 실습 리뷰", description = "유치원 실습 리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class KindergartenInternshipReviewController {
    private final KindergartenFacade kindergartenFacade;
    private final KindergartenInternshipReviewService kindergartenInternshipReviewService;

    @Operation(summary = "실습리뷰-01 리뷰 생성", description = "리뷰 작성")
    @PostMapping("/review")
    public void createInternshipReview(
            @RequestBody CreateInternshipReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenFacade.createInternshipReview(request, userDetails.getUsername());
    }

    @Operation(summary = "실습리뷰-02 리뷰 수정", description = "리뷰 수정")
    @PutMapping("/review")
    public void modifyInternshipReview(
            @RequestBody ModifyInternshipReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenFacade.modifyInternshipReview(request, userDetails.getUsername());
    }

    @Operation(summary = "실습리뷰-03 리뷰 좋아요", description = "리뷰 좋아요")
    @PostMapping("/review/{internshipReviewId}/like")
    public void likeInternshipReview(
            @PathVariable("internshipReviewId") long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenInternshipReviewService.likeInternshipReview(id, userDetails.getUsername());
    }

    @Operation(summary = "실습리뷰-04 리뷰 페이징 조회", description = "리뷰 페이징 조회 (정렬: LATEST-최신순, POPULAR-인기순)")
    @GetMapping("/reviews/{kindergartenId}")
    public InternshipReviewPagedResponseDTO getReviews(
            @PathVariable("kindergartenId") Long kindergartenId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") InternshipReviewPagedResponseDTO.SortType sortType,
            @RequestParam(defaultValue = "ALL") InternshipReviewStarRatingType internshipReviewStarRatingType,
            @RequestParam(defaultValue = "0") int starRating
    ) {
        return kindergartenInternshipReviewService.getReviews(kindergartenId, page, size, sortType, internshipReviewStarRatingType, starRating);
    }

    @Operation(summary = "실습리뷰-05 리뷰 삭제", description = "실습 리뷰를 삭제합니다. 본인이 작성한 리뷰 또는 관리자가 삭제할 수 있습니다.")
    @DeleteMapping("/review/{internshipReviewId}")
    public ResponseDto<String> deleteInternshipReview(
            @PathVariable("internshipReviewId") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenInternshipReviewService.deleteInternshipReview(id, userDetails.getUsername());
        return ResponseDto.success("실습 리뷰가 삭제되었습니다.");
    }

    @Operation(summary = "실습리뷰-06 전체 리뷰 조회", description = "유치원 상관없이 전체 실습 리뷰를 페이징 조회합니다. (정렬: LATEST-최신순, POPULAR-인기순)")
    @GetMapping("/reviews")
    public InternshipReviewPagedResponseDTO getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") InternshipReviewPagedResponseDTO.SortType sortType
    ) {
        return kindergartenInternshipReviewService.getAllReviews(page, size, sortType);
    }

}