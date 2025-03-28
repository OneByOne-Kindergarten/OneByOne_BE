package com.onebyone.kindergarten.domain.communityPosts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CommunitySearchDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {

    @Query("SELECT p FROM CommunityPost p " +
           "JOIN FETCH p.user " +
           "JOIN FETCH p.communityCategory " +
           "WHERE p.id = :id")
    Optional<CommunityPost> findByIdWithUser(@Param("id") Long id);

    @Modifying
    @Query("UPDATE CommunityPost p " +
           "SET p.viewCount = p.viewCount + 1 " +
           "WHERE p.id = :id")
    void increaseViewCount(@Param("id") Long id);

    @Query("SELECT p FROM CommunityPost p " +
           "JOIN FETCH p.user " +
           "JOIN FETCH p.communityCategory " +
           "ORDER BY p.likeCount DESC, p.viewCount DESC " +
           "LIMIT 10")
    List<CommunityPost> findTop10WithUserOrderByLikeCountDescViewCountDesc();

    @Query("""
            SELECT p FROM CommunityPost p 
            JOIN FETCH p.user u 
            JOIN FETCH p.communityCategory c 
            WHERE (:#{#search.title} IS NULL OR p.title LIKE %:#{#search.title}%) 
            AND (:#{#search.content} IS NULL OR p.content LIKE %:#{#search.content}%) 
            AND (:#{#search.category} IS NULL OR p.category = :#{#search.category}) 
            AND (:#{#search.categoryName} IS NULL OR c.categoryName LIKE %:#{#search.categoryName}%) 
            AND (:#{#search.userName} IS NULL OR u.nickname LIKE %:#{#search.userName}%) 
            AND (:#{#search.startDate} IS NULL OR p.createdAt >= :#{#search.startDate}) 
            AND (:#{#search.endDate} IS NULL OR p.createdAt <= :#{#search.endDate})
            """)
    Page<CommunityPost> search(@Param("search") CommunitySearchDTO search, Pageable pageable);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.likeCount = p.likeCount + :delta WHERE p.id = :postId")
    void updateLikeCount(@Param("postId") Long postId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);
}
