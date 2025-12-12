package com.onebyone.kindergarten.domain.communityPosts.repository;

import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCategoryRepository extends JpaRepository<CommunityCategory, Long> {
  // 카테고리 이름으로 조회
  Optional<CommunityCategory> findByCategoryName(String categoryName);
}
