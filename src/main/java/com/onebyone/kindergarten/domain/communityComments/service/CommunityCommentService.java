package com.onebyone.kindergarten.domain.communityComments.service;

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
@Transactional(readOnly = true)
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository postRepository;
    private final UserService userService;

    public CommunityCommentService(CommunityCommentRepository commentRepository, CommunityRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * 댓글 작성
     */
    @Transactional
    public CommentResponseDTO createComment(Long postId, CreateCommentRequestDTO dto, String email) {
        User user = userService.getUserByEmail(email);
        CommunityPost post = postRepository.getReferenceById(postId);

        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(dto.getContent())
                .build();

        CommunityComment savedComment = commentRepository.save(comment);
        
        // 댓글 수 업데이트 (별도 쿼리로 처리하여 락 경쟁 방지)
        postRepository.incrementCommentCount(postId);

        return CommentResponseDTO.fromEntity(savedComment);
    }

    /**
     * 게시글의 댓글 목록 조회
     */
    public Page<CommentResponseDTO> getComments(Long postId, Pageable pageable) {
        return commentRepository.findCommentDTOsByPostId(postId, pageable);
    }
}
