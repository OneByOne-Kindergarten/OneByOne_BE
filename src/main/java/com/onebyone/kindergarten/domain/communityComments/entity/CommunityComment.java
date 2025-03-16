package com.onebyone.kindergarten.domain.communityComments.entity;

import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;

@Entity(name = "community_comment")
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

    private ReportStatus status = ReportStatus.YET; // 댓글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)
}