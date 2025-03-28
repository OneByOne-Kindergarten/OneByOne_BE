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

    /// 게시글 좋아요 상태 조회 및 좋아요 수 조회
    public CommunityLikeResponseDTO getLikeInfo(Long postId, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);
        
        // 한 번의 쿼리로 좋아요 상태와 개수를 함께 조회
        return communityLikeRepository.findLikeInfo(postId, user)
                .orElse(new CommunityLikeResponseDTO(false, 0));
    }

    /// 게시글 좋아요/좋아요 취소 토글
    @Transactional
    public CommunityLikeResponseDTO toggleLike(Long postId, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 게시글 존재 여부와 좋아요 여부를 한 번에 확인
        return communityLikeRepository.findByUserAndPostId(user, postId)
                .map(like -> {
                    // 좋아요 취소
                    communityLikeRepository.delete(like);
                    communityRepository.updateLikeCount(postId, -1);
                    return new CommunityLikeResponseDTO(false, like.getPost().getLikeCount() - 1);
                })
                .orElseGet(() -> {
                    // 게시글 존재 여부 확인 및 좋아요 추가
                    CommunityPost post = communityRepository.findById(postId)
                            .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
                    
                    CommunityLike newLike = CommunityLike.builder()
                            .user(user)
                            .post(post)
                            .build();
                    communityLikeRepository.save(newLike);
                    communityRepository.updateLikeCount(postId, 1);
                    return new CommunityLikeResponseDTO(true, post.getLikeCount() + 1);
                });
    }
} 