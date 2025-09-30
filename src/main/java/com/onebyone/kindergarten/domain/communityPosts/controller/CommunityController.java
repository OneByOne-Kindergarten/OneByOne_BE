package com.onebyone.kindergarten.domain.communityPosts.controller;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CreateCommunityPostRequestDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityPostResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.service.CommunityService;
import com.onebyone.kindergarten.global.facade.CommunityFacade;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.domain.communityPosts.dto.request.CommunitySearchDTO;
import com.onebyone.kindergarten.domain.communityPosts.service.CommunityLikeService;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityLikeResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "커뮤니티 게시글", description = "커뮤니티 게시글 관련 API")
public class CommunityController {
    private final CommunityFacade communityFacade;
    private final CommunityService communityService;
    private final CommunityLikeService communityLikeService;

    @PostMapping()
    @Operation(summary = "커뮤니티 게시글 생성", description = "게시글을 생성합니다.")
    public ResponseDto<CommunityPostResponseDTO> createPost(
            @Valid @RequestBody CreateCommunityPostRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(communityService.createPost(request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "커뮤니티 게시글 삭제", description = "게시글을 삭제합니다. 본인이 작성한 게시글 또는 관리자가 삭제할 수 있습니다.")
    public ResponseDto<String> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        communityFacade.deletePost(id, userDetails.getUsername());
        return ResponseDto.success("게시글이 삭제되었습니다.");
    }

    @GetMapping()
    @Operation(summary = "커뮤니티 게시글 목록 조회", description = "게시글 목록을 조회하고 검색합니다. 차단한 사용자의 게시글은 제외됩니다.")
    public PageResponseDTO<CommunityPostResponseDTO> getPosts(
            CommunitySearchDTO searchDTO,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return new PageResponseDTO<>(communityService.getPosts(searchDTO, pageable, email));
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

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 추가하거나 취소합니다.")
    public ResponseDto<CommunityLikeResponseDTO> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(communityLikeService.toggleLike(postId, userDetails.getUsername()));
    }

    @GetMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 상태 조회", description = "현재 사용자가 게시글에 좋아요를 눌렀는지 확인합니다.")
    public ResponseDto<CommunityLikeResponseDTO> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(communityLikeService.getLikeInfo(postId, userDetails.getUsername()));
    }
}
