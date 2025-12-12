package com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response;

import lombok.Getter;

@Getter
public class FavoriteToggleResponseDTO {
  private final boolean isFavorite;

  public FavoriteToggleResponseDTO(boolean isFavorite) {
    this.isFavorite = isFavorite;
  }
}
