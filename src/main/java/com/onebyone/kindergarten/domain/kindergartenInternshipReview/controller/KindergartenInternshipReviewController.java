package com.onebyone.kindergarten.domain.kindergartenInternshipReview.controller;

import com.onebyone.kindergarten.domain.facade.KindergartenFacade;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.CreateInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.ModifyInternshipReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.service.KindergartenInternshipReviewService;
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

    @Operation(summary = "실습리뷰-04 리뷰 페이징 조회", description = "리뷰 페이징 조회")
    @GetMapping("/reviews/{kindergartenId}")
    public InternshipReviewPagedResponseDTO getReviews(
            @PathVariable("kindergartenId") Long kindergartenId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return kindergartenInternshipReviewService.getReviews(kindergartenId, page, size);
    }
}