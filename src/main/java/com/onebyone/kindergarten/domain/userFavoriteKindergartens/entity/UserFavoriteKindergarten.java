package com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user_favorite_kindergarten")
@Getter
@NoArgsConstructor
public class UserFavoriteKindergarten extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 즐겨찾기 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id", nullable = false)
    private Kindergarten kindergarten; // 유치원

    @Builder
    public UserFavoriteKindergarten(User user, Kindergarten kindergarten) {
        this.user = user;
        this.kindergarten = kindergarten;
    }
}
