package com.onebyone.kindergarten.domain.communityPosts.service;

import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityLikeResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityLike;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.exception.PostNotFoundException;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityLikeRepository;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityLikeService {
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;

    /**
     * 게시글 좋아요 상태 조회 및 좋아요 수 조회
     */
    public CommunityLikeResponseDTO getLikeInfo(Long postId, String email) {
        User user = userService.getUserByEmail(email);
        boolean isLiked = communityLikeRepository.existsByUserAndPost(user,
                communityRepository.getReferenceById(postId));
        int likeCount = communityLikeRepository.countByPostId(postId);
        
        return new CommunityLikeResponseDTO(isLiked, likeCount);
    }

    /**
     * 게시글 좋아요/좋아요 취소 토글
     */
    @Transactional
    public CommunityLikeResponseDTO toggleLike(Long postId, String email) {
        User user = userService.getUserByEmail(email);
        // getReferenceById는 실제 DB 조회 없이 프록시 객체만 생성 (성능 향상)
        CommunityPost post = communityRepository.getReferenceById(postId);

        // 좋아요를 눌렀는지 확인
        return communityLikeRepository.findByUserAndPost(user, post)
                .map(like -> {
                    // 좋아요 취소
                    communityLikeRepository.delete(like);
                    // 단일 필드만 업데이트하는 쿼리 사용 (성능 향상)
                    communityRepository.updateLikeCount(postId, -1);
                    return new CommunityLikeResponseDTO(false, communityLikeRepository.countByPostId(postId));
                })
                .orElseGet(() -> {
                    // 좋아요 추가
                    communityLikeRepository.save(CommunityLike.builder()
                            .user(user)
                            .post(post)
                            .build());
                    communityRepository.updateLikeCount(postId, 1);
                    return new CommunityLikeResponseDTO(true, communityLikeRepository.countByPostId(postId));
                });
    }

    /**
     * 사용자가 게시글에 좋아요를 눌렀는지 확인
     */
    public boolean isLiked(Long postId, String email) {
        User user = userService.getUserByEmail(email);
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return communityLikeRepository.existsByUserAndPost(user, post);
    }
    
    /**
     * 게시글의 좋아요 수 조회
     */
    public int getLikeCount(Long postId) {
        return communityLikeRepository.countByPostId(postId);
    }
} 