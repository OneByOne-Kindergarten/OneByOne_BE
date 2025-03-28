package com.onebyone.kindergarten.domain.communityComments.dto.response;

import java.time.LocalDateTime;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String nickName;
    private String career;    // 경력 정보 (예: "5년 3개월")
    private UserRole userRole;
    private LocalDateTime createdAt;
    private ReportStatus status;

    public CommentResponseDTO(Long id, String content, String nickName,
                            String career, UserRole userRole, 
                            LocalDateTime createdAt, ReportStatus status) {
        this.id = id;
        this.content = content;
        this.nickName = nickName;
        this.career = career;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static CommentResponseDTO fromEntity(CommunityComment comment) {
        User user = comment.getUser();

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickName(user.getNickname())
                .career(user.getCareer())
                .userRole(user.getRole())
                .createdAt(comment.getCreatedAt())
                .status(comment.getStatus())
                .build();
    }
} 