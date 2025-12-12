package com.onebyone.kindergarten.domain.notice.dto.response;

import com.onebyone.kindergarten.domain.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class NoticeResponseDTO {
  private final Long id;
  private final String title;
  private final String content;
  private final boolean isPushSend;
  private final boolean isPublic;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  // JPQL 생성자
  public NoticeResponseDTO(
      Long id,
      String title,
      String content,
      boolean isPushSend,
      boolean isPublic,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.isPushSend = isPushSend;
    this.isPublic = isPublic;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Entity 변환 생성자
  public NoticeResponseDTO(Notice notice) {
    this(
        notice.getId(),
        notice.getTitle(),
        notice.getContent(),
        notice.isPushSend(),
        notice.isPublic(),
        notice.getCreatedAt(),
        notice.getUpdatedAt());
  }

  public static NoticeResponseDTO from(Notice notice) {
    return new NoticeResponseDTO(notice);
  }
}
