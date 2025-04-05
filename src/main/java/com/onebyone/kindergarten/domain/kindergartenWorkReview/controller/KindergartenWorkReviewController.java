package com.onebyone.kindergarten.domain.kindergartenWorkReview.controller;

import com.onebyone.kindergarten.domain.facade.KindergartenFacade;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.CreateWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.service.KindergartenWorkReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유치원 근무 리뷰", description = "유치원 근무 리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/work")
public class KindergartenWorkReviewController {
    private final KindergartenFacade kindergartenFacade;
    private final KindergartenWorkReviewService kindergartenWorkReviewService;

    @Operation(summary = "근무리뷰-01 리뷰 생성", description = "리뷰 작성")
    @PostMapping("/review")
    public void createWorkReview(
            @RequestBody CreateWorkReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenFacade.createWorkReview(request, userDetails.getUsername());
    }

    @Operation(summary = "근무리뷰-02 리뷰 수정", description = "리뷰 수정")
    @PutMapping("/review")
    public void modifyWorkReview(
            @RequestBody ModifyWorkReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenFacade.modifyWorkReview(request, userDetails.getUsername());
    }

    @Operation(summary = "근무리뷰-03 리뷰 좋아요", description = "리뷰 좋아요")
    @PostMapping("/review/{kindergartenInternshipReviewId}/like")
    public void likeWorkReview(
            @PathVariable("kindergartenInternshipReviewId") long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        kindergartenWorkReviewService.likeWorkReview(id, userDetails.getUsername());
    }

    @Operation(summary = "근무리뷰-04 리뷰 페이징 조회", description = "리뷰 페이징 조회")
    @GetMapping("/reviews")
    public WorkReviewPagedResponseDTO getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return kindergartenWorkReviewService.getReviews(page, size);
    }
}