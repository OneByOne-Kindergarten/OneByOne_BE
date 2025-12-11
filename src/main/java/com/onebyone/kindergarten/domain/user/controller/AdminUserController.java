package com.onebyone.kindergarten.domain.user.controller;

import com.onebyone.kindergarten.domain.user.dto.request.UpdateUserStatusRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.UserSearchDTO;
import com.onebyone.kindergarten.domain.user.dto.response.AdminUserResponseDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@Tag(name = "유저 관리", description = "유저 관리 API (관리자용)")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;

    @GetMapping
    @Operation(summary = "전체 유저 목록 조회", description = "모든 유저 목록 리스트 조회")
    public PageResponseDTO<AdminUserResponseDTO> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AdminUserResponseDTO> users = userService.getAllUsers(pageable);
        return new PageResponseDTO<>(users);
    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색", description = "조건부 유저 검색")
    public PageResponseDTO<AdminUserResponseDTO> searchUsers(
            UserSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AdminUserResponseDTO> users = userService.searchUsers(searchDTO, pageable);
        return new PageResponseDTO<>(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "유저 상세 조회", description = "특정 유저 상세 정보 조회")
    public ResponseDto<AdminUserResponseDTO> getUserDetail(
            @PathVariable Long userId
    ) {
        AdminUserResponseDTO user = userService.getUserToAdminDTO(userId);
        return ResponseDto.success(user);
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "유저 상태 변경", description = "관리자가 유저의 상태를 변경합니다. (ACTIVE, SUSPENDED, DELETED)")
    public ResponseDto<String> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUserStatus(userId, request);
        return ResponseDto.success("유저 상태가 변경되었습니다.");
    }
}
