package com.onebyone.kindergarten.domain.communityComments.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.onebyone.kindergarten.domain.communityComments.service.CommunityCommentService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.common.ResponseDto;
import com.onebyone.kindergarten.domain.communityComments.dto.request.CreateCommentRequestDTO;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/{postId}/comment")
@Tag(name = "댓글", description = "게시글의 댓글 관련 API")
public class CommunityCommentController {

    private final CommunityCommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseDto<CommentResponseDTO> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(commentService.createComment(postId, dto, userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다.")
    public PageResponseDTO<CommentResponseDTO> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(commentService.getComments(postId, pageable));
    }

}
