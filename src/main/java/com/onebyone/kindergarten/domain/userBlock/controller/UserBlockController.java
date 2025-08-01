package com.onebyone.kindergarten.domain.userBlock.controller;

import com.onebyone.kindergarten.domain.userBlock.dto.request.UserBlockRequestDto;
import com.onebyone.kindergarten.domain.userBlock.dto.response.BlockedUserResponseDto;
import com.onebyone.kindergarten.domain.userBlock.service.UserBlockService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blocks")
@Tag(name = "유저 차단", description = "유저 차단 관련 API")
public class UserBlockController {
    private final UserBlockService userBlockService;

    @Operation(summary = "유저 차단-01 : 유저 차단", description = "로그인한 유저가 특정 유저를 차단합니다.")
    @PostMapping
    public ResponseDto<Void> blockUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserBlockRequestDto request
    ) {
        userBlockService.blockUser(userDetails, request.getTargetUserEmail());
        return ResponseDto.success(null);
    }

    @Operation(summary = "유저 차단-02 : 유저 차단 해제", description = "로그인한 유저가 차단한 특정 유저를 차단 해제합니다.")
    @DeleteMapping("/{targetUserEmail}")
    public ResponseDto<Void> unblockUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String targetUserEmail
    ) {
        userBlockService.unblockUser(userDetails, targetUserEmail);
        return ResponseDto.success(null);
    }

    @Operation(summary = "유저 차단-03 : 차단된 유저 목록 조회", description = "로그인한 유저가 차단한 유저들의 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseDto<List<BlockedUserResponseDto>> getBlockedUsers(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(userBlockService.getBlockedUsers(userDetails));
    }

}