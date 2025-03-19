package com.onebyone.kindergarten.domain.communityPosts.controller;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CreateCommunityPostRequestDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityPostResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.service.CommunityService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.domain.communityPosts.dto.request.CommunitySearchDTO;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping()
    @Operation(summary = "커뮤니티 게시글 생성", description = "게시글을 생성합니다.")
    public ResponseDto<CommunityPostResponseDTO> createPost(
            @Valid @RequestBody CreateCommunityPostRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(communityService.createPost(request, userDetails.getUsername()));
    }

    @GetMapping()
    @Operation(summary = "커뮤니티 게시글 목록 조회", description = "게시글 목록을 조회하고 검색합니다.")
    public PageResponseDTO<CommunityPostResponseDTO> getPosts(
            CommunitySearchDTO searchDTO,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(communityService.getPosts(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "커뮤니티 게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    public ResponseDto<CommunityPostResponseDTO> getPost(
            @PathVariable Long id
    ) {
        return ResponseDto.success(communityService.getPost(id));
    }

    @GetMapping("/top")
    @Operation(summary = "인기 게시글 TOP 10 조회", description = "좋아요 수와 조회수를 기준으로 인기 게시글 TOP 10을 조회합니다.")
    public ResponseDto<List<CommunityPostResponseDTO>> getTopPosts() {
        return ResponseDto.success(communityService.getTopPosts());
    }
}
