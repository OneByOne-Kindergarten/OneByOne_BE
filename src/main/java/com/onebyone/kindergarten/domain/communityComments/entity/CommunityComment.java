package com.onebyone.kindergarten.domain.communityComments.entity;

import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "community_comment")
@Getter
@NoArgsConstructor
public class CommunityComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post; // 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @Column(nullable = false, length = 500)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.YET; // 댓글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)

    // 대댓글 기능을 위한 부모 댓글 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommunityComment parent; // 부모 댓글 (null이면 원댓글, 값이 있으면 대댓글)

    @Builder
    public CommunityComment(CommunityPost post, User user, String content, CommunityComment parent) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.parent = parent;
    }

    /// 댓글 신고 상태 변경
    public void updateStatus(ReportStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    /// 대댓글 여부 확인
    public boolean isReply() {
        return this.parent != null;
    }

    /// 댓글 소프트 삭제
    public void markAsDeleted() {
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
    }
}