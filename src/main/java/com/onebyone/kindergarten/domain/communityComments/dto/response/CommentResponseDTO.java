package com.onebyone.kindergarten.domain.communityComments.dto.response;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDTO {
  private Long id;

  private String content;

  private String nickName;

  private String email;

  private String career;

  private UserRole userRole;

  private boolean hasWrittenReview;

  private LocalDateTime createdAt;

  private ReportStatus status;

  private Long parentId;

  private boolean isReply;

  public CommentResponseDTO(
      Long id,
      String content,
      String nickName,
      String email,
      String career,
      UserRole userRole,
      boolean hasWrittenReview,
      LocalDateTime createdAt,
      ReportStatus status,
      Long parentId,
      boolean isReply) {
    this.id = id;
    this.content = content;
    this.nickName = nickName;
    this.email = email;
    this.career = career;
    this.userRole = userRole;
    this.hasWrittenReview = hasWrittenReview;
    this.createdAt = createdAt;
    this.status = status;
    this.parentId = parentId;
    this.isReply = isReply;
  }

  public static CommentResponseDTO fromEntity(CommunityComment comment) {
    User user = comment.getUser();

    return CommentResponseDTO.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .nickName(user.getNickname())
        .email(user.getEmail())
        .career(user.getCareer())
        .userRole(user.getRole())
        .hasWrittenReview(user.hasWrittenReview())
        .createdAt(comment.getCreatedAt())
        .status(comment.getStatus())
        .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
        .isReply(comment.isReply())
        .build();
  }
}
