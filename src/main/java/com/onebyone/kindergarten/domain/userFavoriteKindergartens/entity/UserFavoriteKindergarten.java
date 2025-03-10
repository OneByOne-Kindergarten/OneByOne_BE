package com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserFavoriteKindergarten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 즐겨찾기 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id", nullable = false)
    private Kindergarten kindergarten; // 유치원

    private LocalDateTime createdAt; // 추가일
}
