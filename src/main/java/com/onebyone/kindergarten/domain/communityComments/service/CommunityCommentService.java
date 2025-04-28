package com.onebyone.kindergarten.domain.communityComments.service;

import com.onebyone.kindergarten.domain.communityComments.dto.response.PageCommunityCommentsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityComments.dto.request.CreateCommentRequestDTO;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository postRepository;
    private final UserService userService;
    private final NotificationTemplateService notificationTemplateService;

    /// 댓글 작성 (원댓글 또는 대댓글)
    @Transactional
    public CommentResponseDTO createComment(Long postId, CreateCommentRequestDTO dto, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);
        
        // 게시글 조회 (작성자 정보를 포함)
        CommunityPost post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        CommunityComment parent = null;
        
        // 대댓글인 경우 부모 댓글 조회
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("원댓글을 찾을 수 없습니다."));
            
            // 부모 댓글의 게시글과 요청된 게시글이 일치하는지 확인
            if (!parent.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("원댓글의 게시글이 일치하지 않습니다.");
            }
            
            // 이미 대댓글인 경우 대댓글에 대댓글 작성 방지
            if (parent.isReply()) {
                throw new IllegalArgumentException("대댓글에는 답글을 작성할 수 없습니다.");
            }
        }

        // 댓글 작성
        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(dto.getContent())
                .parent(parent)
                .build();
        commentRepository.save(comment);

        // 댓글 수 업데이트 (최상위 댓글인 경우에만)
        if (parent == null) {
            postRepository.incrementCommentCount(postId);
        }

        // 알림 대상자 결정 (게시글 작성자 또는 부모 댓글 작성자)
        User notificationTarget = parent != null ? parent.getUser() : post.getUser();

        // 알림 발송
        notificationTemplateService.sendCommentNotification(
                notificationTarget.getId(), 
                user, 
                dto.getContent(), 
                parent != null, 
                postId
        );
        
        return CommentResponseDTO.fromEntity(comment);
    }

    /// 게시글의 최상위 댓글 목록 조회 (대댓글 제외)
    public Page<CommentResponseDTO> getOriginalComments(Long postId, Pageable pageable) {
        return commentRepository.findOriginalCommentsByPostId(postId, pageable);
    }
    
    /// 특정 댓글의 대댓글 목록 조회
    public List<CommentResponseDTO> getReplies(Long commentId) {
        return commentRepository.findRepliesByParentId(commentId);
    }
    
    /// 게시글의 모든 댓글과 대댓글 목록 조회 (계층 구조로 정렬)
    public Page<CommentResponseDTO> getAllCommentsWithReplies(Long postId, Pageable pageable) {
        return commentRepository.findAllCommentsWithRepliesByPostId(postId, pageable);
    }

    @Transactional(readOnly = true)
    public PageCommunityCommentsResponseDTO getWroteMyCommunityComments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<CommentResponseDTO> commentsPage = commentRepository.findByUserId(userId, pageable)
                .map(CommentResponseDTO::fromEntity);

        PageCommunityCommentsResponseDTO response = new PageCommunityCommentsResponseDTO();
        response.setContent(commentsPage.getContent());
        response.setTotalPages(commentsPage.getTotalPages());

        return response;
    }
}
