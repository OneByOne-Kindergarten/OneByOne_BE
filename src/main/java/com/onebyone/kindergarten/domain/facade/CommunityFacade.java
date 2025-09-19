package com.onebyone.kindergarten.domain.facade;

import com.onebyone.kindergarten.domain.communityPosts.service.CommunityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityFacade {
    private final CommunityService communityService;

    @Transactional
    public void deletePost(Long id, String username) {
        communityService.deletePost(id, username);
        communityService.refreshTopPostsCache();
    }

}
