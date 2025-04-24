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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/{postId}/comment")
@Tag(name = "댓글", description = "게시글의 댓글 관련 API")
public class CommunityCommentController {

    private final CommunityCommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 작성", description = "게시글에 댓글이나 대댓글을 작성합니다. 대댓글인 경우 parentId에 원댓글 ID를 입력합니다.")
    public ResponseDto<CommentResponseDTO> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(commentService.createComment(postId, dto, userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "원댓글 목록 조회", description = "게시글의 원댓글 목록을 조회합니다 (대댓글 제외).")
    public PageResponseDTO<CommentResponseDTO> getOriginalComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(commentService.getOriginalComments(postId, pageable));
    }
    
    @GetMapping("/replies/{commentId}")
    @Operation(summary = "대댓글 목록 조회", description = "특정 원댓글에 대한 대댓글 목록을 조회합니다.")
    public ResponseDto<List<CommentResponseDTO>> getReplies(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return ResponseDto.success(commentService.getReplies(commentId));
    }
    
    @GetMapping("/all")
    @Operation(summary = "모든 댓글 조회", description = "게시글의 모든 댓글과 대댓글을 함께 조회합니다.")
    public PageResponseDTO<CommentResponseDTO> getAllComments(
            @PathVariable Long postId,
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponseDTO<>(commentService.getAllCommentsWithReplies(postId, pageable));
    }
}
