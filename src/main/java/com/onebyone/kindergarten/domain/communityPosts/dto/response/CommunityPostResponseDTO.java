package com.onebyone.kindergarten.domain.communityPosts.dto.response;
import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityPostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private PostCategory category;
    private String categoryName;
    private String categoryDescription;
    private String userName;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 