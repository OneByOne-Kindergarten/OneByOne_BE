package com.onebyone.kindergarten.domain.communityComments.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageCommunityCommentsResponseDTO {
    private List<CommentResponseDTO> content;
    private int totalPages;
}
