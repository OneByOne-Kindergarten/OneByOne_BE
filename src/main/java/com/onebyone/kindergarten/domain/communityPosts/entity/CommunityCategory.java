package com.onebyone.kindergarten.domain.communityPosts.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "community_category")
public class CommunityCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName; // 카테고리 이름

    @Column(length = 500)
    private String description; // 카테고리 설명

    @Column(name = "display_order", nullable = true)
    private Integer displayOrder; // 정렬을 위한 순서

    @Column(nullable = false)
    private Boolean isActive = true; // 활성화 여부


    @Builder
    public CommunityCategory(String categoryName, String description, Integer displayOrder, Boolean isActive) {
        this.categoryName = categoryName;
        this.description = description;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

} 