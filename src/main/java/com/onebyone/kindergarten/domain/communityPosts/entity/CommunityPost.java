package com.onebyone.kindergarten.domain.communityPosts.entity;

import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntity {
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CommunityCategory communityCategory; // 커뮤니티 카테고리

  @Column(name = "like_count")
  private Integer likeCount = 0; // 좋아요 수

  @Column(name = "comment_count")
  private Integer commentCount = 0; // 댓글 수

  @Enumerated(EnumType.STRING)
  private ReportStatus status = ReportStatus.YET; // 게시글 상태 - 차단 여부 (PENDING, PROCESSED, REJECTED)

  @Column(name = "view_count")
  private Integer viewCount = 0; // 조회수

  @Builder
  public CommunityPost(
      User user,
      String title,
      String content,
      PostCategory category,
      CommunityCategory communityCategory) {
    this.user = user;
    this.title = title;
    this.content = content;
    this.category = category;
    this.communityCategory = communityCategory;
  }

  public void setCategory(CommunityCategory category) {
    this.communityCategory = category;
  }

  // 좋아요 증가
  public void increaseLikeCount() {
    this.likeCount++;
  }

  // 좋아요 감소
  public void decreaseLikeCount() {
    this.likeCount--;
  }

  /// 게시물 신고 상태 변경
  public void updateStatus(ReportStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  /// 게시물 소프트 삭제
  public void markAsDeleted() {
    this.updatedAt = LocalDateTime.now();
    this.deletedAt = LocalDateTime.now();
  }
}
