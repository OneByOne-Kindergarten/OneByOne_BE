package com.onebyone.kindergarten.domain.address.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sub_region")
public class SubRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_region_id")
    private Long subRegionId;

    private String name; // 수원시, 장안구

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 경기도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SubRegion parent; // 장안구 parent (수원시)
}
