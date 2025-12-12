package com.onebyone.kindergarten.domain.notice.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 공지 코드

  @Column(nullable = false, length = 100)
  private String title; // 공지 제목

  @Column(nullable = false, length = 2000)
  private String content; // 공지 내용

  @Column(name = "is_push_send")
  private boolean isPushSend; // 푸시 발송 여부

  @Column(name = "is_public")
  private boolean isPublic; // 공지 공개 여부

  @Builder
  public Notice(String title, String content, boolean isPushSend, boolean isPublic) {
    this.title = title;
    this.content = content;
    this.isPushSend = isPushSend;
    this.isPublic = isPublic;
  }

  public void togglePublicStatus() {
    this.isPublic = !this.isPublic;
    this.updatedAt = LocalDateTime.now();
  }
}
