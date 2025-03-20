package com.onebyone.kindergarten.domain.communityPosts.entity;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
    // 유니크 제약 조건 추가
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_community_like",
            columnNames = {"user_id", "post_id"}
        )
    }
)
public class CommunityLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @Builder
    public CommunityLike(User user, CommunityPost post) {
        this.user = user;
        this.post = post;
    }
}
