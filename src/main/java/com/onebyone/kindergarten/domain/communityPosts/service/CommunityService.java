package com.onebyone.kindergarten.domain.communityPosts.service;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CommunitySearchDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.request.CreateCommunityPostRequestDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityPostResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityCategory;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.mapper.CommunityPostMapper;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityCategoryRepository;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userBlock.repository.UserBlockRepository;
import com.onebyone.kindergarten.global.config.CacheConfig;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityCategoryRepository communityCategoryRepository;
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;
    private final CommunityPostMapper communityPostMapper;

    /// 게시글 생성
    @Transactional
    public CommunityPostResponseDTO createPost(CreateCommunityPostRequestDTO request, Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);

        // 커뮤니티 카테고리 조회 또는 생성
        CommunityCategory communityCategory = communityCategoryRepository.findByCategoryName(request.getCommunityCategoryName())
                .orElseGet(() -> communityCategoryRepository.save(
                        CommunityCategory.builder()
                                .categoryName(request.getCommunityCategoryName())
                                .description(request.getCommunityCategoryDescription())
                                .isActive(true)
                                .build()
                ));

        // 게시글 생성
        CommunityPost savedPost = communityRepository.save(communityPostMapper.toEntity(request, user, communityCategory));
        return CommunityPostResponseDTO.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .userNickname(user.getNickname())
                .build();
    }

    /// 게시글 목록 조회
    public Page<CommunityPostResponseDTO> getPosts(CommunitySearchDTO searchDTO, Pageable pageable, Long userId) {

        // 차단된 사용자 ID 목록 가져오기
        List<Long> blockedUserIds = Collections.emptyList();
        if (userId != null) {
            User user = userService.getUserById(userId);
            blockedUserIds = userBlockRepository.findBlockedUserIdsByUserId(user.getId());
        }

        // 차단된 사용자가 없는 경우 존재하지 않는 ID를 사용
        List<Long> finalBlockedUserIds = blockedUserIds.isEmpty() ? List.of(-1L) : blockedUserIds;
        return communityRepository.search(searchDTO, finalBlockedUserIds, pageable)
                .map(communityPostMapper::toResponse);
    }

    /// 게시글 상세 조회
    @Transactional
    public CommunityPostResponseDTO getPost(Long id) {

        // 게시글 조회 (User 정보 포함)
        CommunityPost post = communityRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_POST));
        
        // 게시글이 존재할 때만 조회수 증가
        communityRepository.increaseViewCount(id);
        
        return communityPostMapper.toResponse(post);
    }

    /// 인기 게시글 TOP 10 조회
    @Cacheable(value = CacheConfig.TOP_POSTS_CACHE)
    public List<CommunityPostResponseDTO> getTopPosts() {
        return communityRepository.findTop10WithUserOrderByLikeCountDescViewCountDesc()
                .stream()
                .map(communityPostMapper::toResponse)
                .collect(Collectors.toList());
    }

    /// 인기 게시글 캐시 갱신
    @CacheEvict(value = CacheConfig.TOP_POSTS_CACHE, allEntries = true)
    public void refreshTopPostsCache() {}

    /// 게시글 삭제 (소프트 삭제)
    @Transactional
    public void deletePost(Long postId, Long userId) {

        // 게시글 조회 (작성자 정보 포함)
        CommunityPost post = communityRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_POST));
        
        // 현재 사용자 조회
        User currentUser = userService.getUserById(userId);
        
        // 작성자 또는 관리자 권한 확인
        if (!post.getUser().getId().equals(userId) && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }
        
        // 게시글 소프트 삭제 (deletedAt 설정)
        post.markAsDeleted();
    }

}
