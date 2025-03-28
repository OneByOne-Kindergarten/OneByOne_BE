package com.onebyone.kindergarten.domain.communityPosts.repository;

import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityLike;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityLikeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    @Query("SELECT new com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityLikeResponseDTO(" +
            "CASE WHEN COUNT(cl2) > 0 THEN true ELSE false END, " +
            "CAST(COUNT(cl) AS int)) " +
            "FROM CommunityPost cp " +
            "LEFT JOIN CommunityLike cl ON cl.post = cp " +
            "LEFT JOIN CommunityLike cl2 ON cl2.post = cp AND cl2.user = :user " +
            "WHERE cp.id = :postId " +
            "GROUP BY cp")
    Optional<CommunityLikeResponseDTO> findLikeInfo(@Param("postId") Long postId, @Param("user") User user);

    @Query("SELECT cl FROM CommunityLike cl " +
           "WHERE cl.user = :user AND cl.post.id = :postId")
    Optional<CommunityLike> findByUserAndPostId(
            @Param("user") User user, 
            @Param("postId") Long postId
    );

    boolean existsByUserAndPost(User user, CommunityPost post);

    @Query("SELECT COUNT(cl) FROM CommunityLike cl WHERE cl.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);

} 