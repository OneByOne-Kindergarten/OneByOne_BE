package com.onebyone.kindergarten.domain.communityComments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityComments.dto.request.CreateCommentRequestDTO;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository postRepository;
    private final UserService userService;

    /// 댓글 작성
    @Transactional
    public CommentResponseDTO createComment(Long postId, CreateCommentRequestDTO dto, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);
        
        // 게시글 - 프록시 객체 조회
        CommunityPost post = postRepository.getReferenceById(postId);

        // 댓글 작성
        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(dto.getContent())
                .build();
        commentRepository.save(comment);

        // 댓글 수 업데이트
        postRepository.incrementCommentCount(postId);
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickName(user.getNickname())
                .career(user.getCareer())
                .userRole(user.getRole())
                .createdAt(comment.getCreatedAt())
                .status(comment.getStatus())
                .build();
    }

    /// 게시글의 댓글 목록 조회
    public Page<CommentResponseDTO> getComments(Long postId, Pageable pageable) {
        return commentRepository.findCommentDTOsByPostId(postId, pageable);
    }
}
