package com.onebyone.kindergarten.domain.kindergartenWorkReview.controller;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.CreateWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.ModifyWorkReviewRequestDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.enums.WorkReviewStarRatingType;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.service.KindergartenWorkReviewService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.global.facade.KindergartenFacade;
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
      @AuthenticationPrincipal UserDetails userDetails) {
    kindergartenFacade.createWorkReview(request, Long.valueOf(userDetails.getUsername()));
  }

  @Operation(summary = "근무리뷰-02 리뷰 수정", description = "리뷰 수정")
  @PutMapping("/review")
  public void modifyWorkReview(
      @RequestBody ModifyWorkReviewRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    kindergartenFacade.modifyWorkReview(request, Long.valueOf(userDetails.getUsername()));
  }

  @Operation(summary = "근무리뷰-03 리뷰 좋아요", description = "리뷰 좋아요")
  @PostMapping("/review/{workReviewId}/like")
  public void likeWorkReview(
      @PathVariable("workReviewId") long id, @AuthenticationPrincipal UserDetails userDetails) {
    kindergartenWorkReviewService.likeWorkReview(id, Long.valueOf(userDetails.getUsername()));
  }

  @Operation(summary = "근무리뷰-04 리뷰 페이징 조회", description = "리뷰 페이징 조회 (정렬: LATEST-최신순, POPULAR-인기순)")
  @GetMapping("/reviews/{kindergartenId}")
  public WorkReviewPagedResponseDTO getReviews(
      @PathVariable long kindergartenId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "LATEST") WorkReviewPagedResponseDTO.SortType sortType,
      @RequestParam(defaultValue = "ALL") WorkReviewStarRatingType internshipReviewStarRatingType,
      @RequestParam(defaultValue = "0") int starRating) {
    return kindergartenWorkReviewService.getReviews(
        kindergartenId, page, size, sortType, internshipReviewStarRatingType, starRating);
  }

  @Operation(
      summary = "근무리뷰-05 리뷰 삭제",
      description = "근무 리뷰를 삭제합니다. 본인이 작성한 리뷰 또는 관리자가 삭제할 수 있습니다.")
  @DeleteMapping("/review/{workReviewId}")
  public ResponseDto<String> deleteWorkReview(
      @PathVariable("workReviewId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
    kindergartenFacade.deleteWorkReview(id, Long.valueOf(userDetails.getUsername()));
    return ResponseDto.success("근무 리뷰가 삭제되었습니다.");
  }

  @Operation(
      summary = "근무리뷰-06 전체 리뷰 조회",
      description = "유치원 상관없이 전체 근무 리뷰를 페이징 조회합니다. (정렬: LATEST-최신순, POPULAR-인기순)")
  @GetMapping("/reviews")
  public WorkReviewPagedResponseDTO getAllReviews(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "LATEST") WorkReviewPagedResponseDTO.SortType sortType) {
    return kindergartenWorkReviewService.getAllReviews(page, size, sortType);
  }
}
