package com.onebyone.kindergarten.domain.communityComments.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class PageCommunityCommentsResponseDTO {
  private List<CommentResponseDTO> content;
  private int totalPages;
}
