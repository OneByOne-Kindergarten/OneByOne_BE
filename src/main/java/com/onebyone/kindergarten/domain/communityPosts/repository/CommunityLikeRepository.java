package com.onebyone.kindergarten.domain.communityPosts.repository;

import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityLike;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    Optional<CommunityLike> findByUserAndPost(User user, CommunityPost post);
    boolean existsByUserAndPost(User user, CommunityPost post);

    @Query("SELECT COUNT(cl) FROM CommunityLike cl WHERE cl.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);

} 