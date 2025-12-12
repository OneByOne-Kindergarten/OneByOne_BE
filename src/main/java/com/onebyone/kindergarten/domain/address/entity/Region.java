package com.onebyone.kindergarten.domain.address.entity;

import com.onebyone.kindergarten.domain.address.dto.RegionDTO;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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

  public static RegionDTO toDto(Region region) {
    RegionDTO regionDTO = new RegionDTO();
    regionDTO.setId(region.getRegionId());
    regionDTO.setName(region.getName());

    return regionDTO;
  }
}
