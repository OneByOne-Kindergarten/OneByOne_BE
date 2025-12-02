package com.onebyone.kindergarten.domain.address.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionId;

    private String name;

    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<SubRegion> subRegions = new ArrayList<>();
}
