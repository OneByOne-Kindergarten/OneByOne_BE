package com.onebyone.kindergarten.domain.communityComments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;

import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
           "c.id, c.content, u.nickname, u.career, u.role, c.createdAt, c.status) " +
           "FROM community_comment c " +
           "JOIN c.user u " +
           "WHERE c.post.id = :postId " +
           "ORDER BY c.createdAt DESC")
    Page<CommentResponseDTO> findCommentDTOsByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM community_comment c " +
           "JOIN FETCH c.user " +
           "WHERE c.id = :commentId")
    Optional<CommunityComment> findByIdWithUser(@Param("commentId") Long commentId);

}
