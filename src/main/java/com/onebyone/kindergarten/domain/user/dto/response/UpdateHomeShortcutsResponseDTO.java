package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHomeShortcutsResponseDTO {
  private boolean success;
  private String message;

  public static UpdateHomeShortcutsResponseDTO success() {
    return UpdateHomeShortcutsResponseDTO.builder()
        .success(true)
        .message("홈 바로가기 정보가 성공적으로 업데이트되었습니다.")
        .build();
  }
}
