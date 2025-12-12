package com.onebyone.kindergarten.domain.inquires.dto.response;

import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class InquiryResponseDTO {
  private final Long id;
  private final String title;
  private final String content;
  private final String answer;
  private final InquiryStatus status;
  private final LocalDateTime createdAt;
  private final Long userId;
  private final String userNickname;
  private final UserRole userRole;

  public InquiryResponseDTO(
      Long id,
      String title,
      String content,
      String answer,
      InquiryStatus status,
      LocalDateTime createdAt,
      Long userId,
      String userNickname,
      UserRole userRole) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.answer = answer;
    this.status = status;
    this.createdAt = createdAt;
    this.userId = userId;
    this.userNickname = userNickname;
    this.userRole = userRole;
  }

  public static InquiryResponseDTO fromEntity(Inquiry inquiry) {
    return new InquiryResponseDTO(
        inquiry.getId(),
        inquiry.getTitle(),
        inquiry.getContent(),
        inquiry.getAnswer(),
        inquiry.getStatus(),
        inquiry.getCreatedAt(),
        inquiry.getUser().getId(),
        inquiry.getUser().getNickname(),
        inquiry.getUser().getRole());
  }
}
