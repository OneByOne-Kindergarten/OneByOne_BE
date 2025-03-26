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
import lombok.RequiredArgsConstructor;
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
                .userName(user.getNickname())
                .build();
    }

    public Page<CommunityPostResponseDTO> getPosts(CommunitySearchDTO searchDTO, Pageable pageable) {
        return communityRepository.search(searchDTO, pageable)
                .map(communityPostMapper::toResponse);
    }

    @Transactional
    public CommunityPostResponseDTO getPost(Long id) {
        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        // 조회수 증가
        communityRepository.increaseViewCount(id);
        
        return communityPostMapper.toResponse(post);
    }

    public List<CommunityPostResponseDTO> getTopPosts() {
        return communityRepository.findTop10ByOrderByLikeCountDescViewCountDesc()
                .stream()
                .map(communityPostMapper::toResponse)
                .collect(Collectors.toList());
    }
}
