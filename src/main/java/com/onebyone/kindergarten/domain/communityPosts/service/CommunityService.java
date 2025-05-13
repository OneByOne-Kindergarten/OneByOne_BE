package com.onebyone.kindergarten.domain.communityPosts.service;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CommunitySearchDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.request.CreateCommunityPostRequestDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityPostResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityCategory;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.exception.PostNotFoundException;
import com.onebyone.kindergarten.domain.communityPosts.mapper.CommunityPostMapper;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityCategoryRepository;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityCategoryRepository communityCategoryRepository;
    private final UserService userService;

    private final CommunityPostMapper communityPostMapper;

    /// 게시글 생성
    @Transactional
    public CommunityPostResponseDTO createPost(CreateCommunityPostRequestDTO request, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

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
    public Page<CommunityPostResponseDTO> getPosts(CommunitySearchDTO searchDTO, Pageable pageable) {
        return communityRepository.search(searchDTO, pageable)
                .map(communityPostMapper::toResponse);
    }

    /// 게시글 상세 조회
    @Transactional
    public CommunityPostResponseDTO getPost(Long id) {

        // 게시글 조회 (User 정보 포함)
        CommunityPost post = communityRepository.findByIdWithUser(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        
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

}
