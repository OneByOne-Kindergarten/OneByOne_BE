package com.onebyone.kindergarten.domain.address.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SubRegionDTO {
  private Long subRegionId;
  private Long regionId;
  private String name;
  private Long parentId;
  private List<SubRegionDTO> children = new ArrayList<>();
}
