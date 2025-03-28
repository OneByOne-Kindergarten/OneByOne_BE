package com.onebyone.kindergarten.domain.inquires.controller;

import com.onebyone.kindergarten.domain.inquires.dto.request.AnswerInquiryRequestDTO;
import com.onebyone.kindergarten.domain.inquires.dto.request.CreateInquiryRequestDTO;
import com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.inquires.service.InquiryService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
@Tag(name = "문의하기", description = "문의하기 관련 API")
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping
    @Operation(summary = "문의 생성", description = "새로운 문의를 생성합니다.")
    public ResponseDto<InquiryResponseDTO> createInquiry(
            @Valid @RequestBody CreateInquiryRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(inquiryService.createInquiry(dto, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "문의 상세 조회", description = "문의 상세 정보를 조회합니다.")
    public ResponseDto<InquiryResponseDTO> getInquiry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(inquiryService.getInquiry(id, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @Operation(summary = "내 문의 목록 조회", description = "내가 작성한 문의 목록을 조회합니다.")
    public PageResponseDTO<InquiryResponseDTO> getMyInquiries(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new PageResponseDTO<>(inquiryService.getUserInquiries(userDetails.getUsername(), pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "모든 문의 목록 조회", description = "모든 문의 목록을 조회합니다. (관리자 전용)")
    public PageResponseDTO<InquiryResponseDTO> getAllInquiries(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new PageResponseDTO<>(inquiryService.getAllInquiries(userDetails.getUsername(), pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "상태별 문의 목록 조회", description = "상태별 문의 목록을 조회합니다. (관리자 전용)")
    public PageResponseDTO<InquiryResponseDTO> getInquiriesByStatus(
            @PathVariable InquiryStatus status,
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new PageResponseDTO<>(inquiryService.getInquiriesByStatus(status, userDetails.getUsername(), pageable));
    }

    @PostMapping("/{id}/answer")
    @Operation(summary = "문의 답변", description = "문의에 답변합니다. (관리자 전용)")
    public ResponseDto<InquiryResponseDTO> answerInquiry(
            @PathVariable Long id,
            @Valid @RequestBody AnswerInquiryRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(inquiryService.answerInquiry(id, dto, userDetails.getUsername()));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "문의 마감", description = "문의를 마감합니다. (관리자 전용)")
    public ResponseDto<InquiryResponseDTO> closeInquiry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(inquiryService.closeInquiry(id, userDetails.getUsername()));
    }
}
