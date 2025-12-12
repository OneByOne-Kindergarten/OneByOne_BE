package com.onebyone.kindergarten.domain.kindergartenWorkHistories.controller;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryRequest;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.service.KindergartenWorkHistoryService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.global.facade.KindergartenWorkHistoryFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유치원 근무 이력", description = "유치원 근무 이력 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class KindergartenWorkHistoryController {
  private final KindergartenWorkHistoryFacade kindergartenWorkHistoryFacade;
  private final KindergartenWorkHistoryService workHistoryService;

  @PostMapping
  @Operation(summary = "유치원 근무 이력 추가", description = "유치원 근무 이력을 추가합니다.")
  public ResponseDto<KindergartenWorkHistoryResponse> addCertification(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody KindergartenWorkHistoryRequest request) {
    return ResponseDto.success(
        kindergartenWorkHistoryFacade.addCertification(
            Long.valueOf(userDetails.getUsername()), request));
  }

  @GetMapping
  @Operation(summary = "유치원 근무 이력 조회", description = "유치원 근무 이력을 조회합니다.")
  public ResponseDto<List<KindergartenWorkHistoryResponse>> getCertification(
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseDto.success(
        workHistoryService.getCertification(Long.valueOf(userDetails.getUsername())));
  }

  @DeleteMapping("/{certificationId}")
  @Operation(summary = "유치원 근무 이력 삭제", description = "유치원 근무 이력을 삭제합니다.")
  public ResponseDto<Void> deleteCertification(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long certificationId) {
    kindergartenWorkHistoryFacade.deleteCertification(
        Long.valueOf(userDetails.getUsername()), certificationId);
    return ResponseDto.success(null);
  }
}
