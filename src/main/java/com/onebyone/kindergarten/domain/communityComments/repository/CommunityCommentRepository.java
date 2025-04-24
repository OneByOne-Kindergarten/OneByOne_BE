package com.onebyone.kindergarten.domain.communityComments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    // 게시글의 원댓글 목록 조회 (parent IS NULL)
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.career, u.role, c.createdAt, c.status, " +
            "null, false) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "WHERE c.post.id = :postId AND c.parent IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<CommentResponseDTO> findOriginalCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // 특정 원댓글에 대한 대댓글 목록 조회
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.career, u.role, c.createdAt, c.status, " +
            "c.parent.id, true) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "WHERE c.parent.id = :parentId " +
            "ORDER BY c.createdAt ASC")
    List<CommentResponseDTO> findRepliesByParentId(@Param("parentId") Long parentId);

    // 게시글의 모든 댓글 조회 (대댓글 포함) - 최적화 버전
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.career, u.role, c.createdAt, c.status, " +
            "c.parent.id, CASE WHEN c.parent IS NOT NULL THEN true ELSE false END) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "LEFT JOIN c.parent p " +
            "WHERE c.post.id = :postId " +
            "ORDER BY COALESCE(p.createdAt, c.createdAt) DESC, " +
            "CASE WHEN c.parent IS NULL THEN 0 ELSE 1 END, " +
            "c.createdAt ASC")
    Page<CommentResponseDTO> findAllCommentsWithRepliesByPostId(@Param("postId") Long postId, Pageable pageable);

}
