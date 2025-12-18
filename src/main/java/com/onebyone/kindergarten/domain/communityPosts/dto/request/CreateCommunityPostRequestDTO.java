package com.onebyone.kindergarten.domain.communityPosts.dto.request;

import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCommunityPostRequestDTO {
  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  @Size(max = 2000, message = "내용은 2000자를 넘을 수 없습니다.")
  private String content;

  @NotNull(message = "카테고리는 필수입니다.") private PostCategory category;

  @NotNull(message = "커뮤니티 카테고리는 필수입니다.") private String communityCategoryName;

  @NotNull(message = "커뮤니티 카테고리 설명은 필수입니다.") private String communityCategoryDescription;
}
