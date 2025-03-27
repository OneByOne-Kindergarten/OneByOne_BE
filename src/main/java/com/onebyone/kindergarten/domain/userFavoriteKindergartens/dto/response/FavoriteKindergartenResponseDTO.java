package com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response;

import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FavoriteKindergartenResponseDTO {
    private final Long id;
    private final Long kindergartenId;
    private final String kindergartenName;
    private final String kindergartenAddress;
    private final LocalDateTime createdAt;

    public FavoriteKindergartenResponseDTO(
            Long id, 
            Long kindergartenId, 
            String kindergartenName, 
            String kindergartenAddress,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.kindergartenId = kindergartenId;
        this.kindergartenName = kindergartenName;
        this.kindergartenAddress = kindergartenAddress;
        this.createdAt = createdAt;
    }

    public static FavoriteKindergartenResponseDTO fromEntity(UserFavoriteKindergarten favorite) {
        return new FavoriteKindergartenResponseDTO(
                favorite.getId(),
                favorite.getKindergarten().getId(),
                favorite.getKindergarten().getName(),
                favorite.getKindergarten().getAddress(),
                favorite.getCreatedAt()
        );
    }
} 