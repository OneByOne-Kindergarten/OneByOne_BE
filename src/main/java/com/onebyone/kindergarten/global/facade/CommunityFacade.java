package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.communityPosts.service.CommunityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityFacade {
  private final CommunityService communityService;

  @Transactional
  public void deletePost(Long id, Long userId) {
    communityService.deletePost(id, userId);
    communityService.refreshTopPostsCache();
  }
}
