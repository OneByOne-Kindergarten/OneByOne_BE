package com.onebyone.kindergarten.domain.communityComments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    // 게시글의 원댓글 목록 조회 (parent IS NULL)
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.email, u.career, u.role, c.createdAt, c.status, " +
            "null, false) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "WHERE c.post.id = :postId AND c.parent IS NULL AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<CommentResponseDTO> findOriginalCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // 특정 원댓글에 대한 대댓글 목록 조회
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.email, u.career, u.role, c.createdAt, c.status, " +
            "c.parent.id, true) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "WHERE c.parent.id = :parentId AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt ASC")
    List<CommentResponseDTO> findRepliesByParentId(@Param("parentId") Long parentId);

    // 게시글의 모든 댓글 조회 (대댓글 포함) - 최적화 버전
    @Query("SELECT new com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO(" +
            "c.id, c.content, u.nickname, u.email ,u.career, u.role, c.createdAt, c.status, " +
            "c.parent.id, CASE WHEN c.parent IS NOT NULL THEN true ELSE false END) " +
            "FROM community_comment c " +
            "JOIN c.user u " +
            "LEFT JOIN c.parent p " +
            "WHERE c.post.id = :postId AND c.deletedAt IS NULL " +
            "AND (u.id NOT IN :blockedUserIds OR :blockedUserIds IS NULL) " +
            "AND (p IS NULL OR p.deletedAt IS NULL) " +
            "AND (p IS NULL OR p.user.id NOT IN :blockedUserIds OR :blockedUserIds IS NULL) " +
            "ORDER BY COALESCE(p.createdAt, c.createdAt) DESC, " +
            "CASE WHEN c.parent IS NULL THEN 0 ELSE 1 END, " +
            "c.createdAt ASC")
    Page<CommentResponseDTO> findAllCommentsWithRepliesByPostId(
            @Param("postId") Long postId,
            @Param("blockedUserIds") List<Long> blockedUserIds,
            Pageable pageable);


    @Query("SELECT c FROM community_comment c WHERE c.user.id = :userId AND c.deletedAt IS NULL")
    Page<CommunityComment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM community_comment c JOIN FETCH c.user WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CommunityComment> findByIdWithUser(@Param("id") Long id);

    @Modifying
    @Query("UPDATE community_comment c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.parent.id = :parentId")
    void updateRepliesDeletedAt(@Param("parentId") Long parentId);
}
