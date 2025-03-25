package com.onebyone.kindergarten.domain.communityComments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
           "c.id, c.content, u.nickname, u.career, u.role, c.createdAt, c.status) " +
           "FROM community_comment c " +
           "JOIN c.user u " +
           "WHERE c.post.id = :postId " +
           "ORDER BY c.createdAt DESC")
    Page<CommentResponseDTO> findCommentDTOsByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM community_comment c WHERE c.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);
}
