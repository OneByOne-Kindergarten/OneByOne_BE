package com.onebyone.kindergarten.domain.communityComments.controller;

import com.onebyone.kindergarten.domain.communityComments.service.CommunityCommentService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "댓글 관리", description = "독립적인 댓글 관리 API")
public class CommentController {

    private final CommunityCommentService commentService;

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. 본인이 작성한 댓글 또는 관리자가 삭제할 수 있습니다. 원댓글 삭제 시 대댓글도 함께 삭제됩니다.")
    public ResponseDto<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(commentId, Long.valueOf(userDetails.getUsername()));
        return ResponseDto.success("댓글이 삭제되었습니다.");
    }
} 