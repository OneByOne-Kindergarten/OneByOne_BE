package com.onebyone.kindergarten.domain.address.entity;

import com.onebyone.kindergarten.domain.address.dto.SubRegionDTO;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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

    public Long getRegionId() {
        return region != null ? region.getRegionId() : null;
    }

    public static SubRegionDTO toDTO(SubRegion subRegion) {
        SubRegionDTO dto = new SubRegionDTO();
        dto.setSubRegionId(subRegion.getSubRegionId());
        dto.setRegionId(subRegion.getRegionId());
        dto.setName(subRegion.getName());
        dto.setParentId(subRegion.getParent() != null ? subRegion.getParent().getSubRegionId() : null);

        return dto;
    }
}
