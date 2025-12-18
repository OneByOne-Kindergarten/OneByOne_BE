package com.onebyone.kindergarten.domain.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeShortcutsDto {

  @Valid
  @NotNull(message = "바로가기 목록은 null이 될 수 없습니다") private List<ShortcutItem> shortcuts;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ShortcutItem {
    @NotBlank(message = "바로가기 이름은 필수입니다")
    @Size(max = 50, message = "바로가기 이름은 50자를 초과할 수 없습니다")
    private String name; // 바로가기 이름

    @NotBlank(message = "아이콘명은 필수입니다")
    @Size(max = 100, message = "아이콘명은 100자를 초과할 수 없습니다")
    private String iconName; // 바로가기 아이콘명

    @NotBlank(message = "링크는 필수입니다")
    @Size(max = 100, message = "링크는 100자를 초과할 수 없습니다")
    private String link; // 바로가기 링크
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private List<ShortcutItem> shortcuts;

    public static Response from(HomeShortcutsDto dto) {
      return Response.builder().shortcuts(dto.getShortcuts()).build();
    }
  }

  public String toJson() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 변환 중 오류가 발생했습니다.", e);
    }
  }

  public static HomeShortcutsDto fromJson(String json) {
    try {
      if (json == null || json.isEmpty()) {
        return new HomeShortcutsDto(new ArrayList<>());
      }

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, HomeShortcutsDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.", e);
    }
  }
}
