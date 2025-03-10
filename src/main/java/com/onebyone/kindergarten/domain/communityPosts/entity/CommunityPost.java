package com.onebyone.kindergarten.domain.communityPosts.entity;

import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CommunityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @Column(nullable = false, length = 100)
    private String title; // 제목

    @Column(nullable = false, length = 2000)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private PostCategory category; // 게시글 카테고리 - 선생님, 예비

    private Integer likeCount = 0; // 좋아요 수
    private Integer commentCount = 0; // 댓글 수

    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 수정일

    private ReportStatus status = ReportStatus.YET; // 게시글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)

    private LocalDateTime deletedAt; // 삭제일
}
