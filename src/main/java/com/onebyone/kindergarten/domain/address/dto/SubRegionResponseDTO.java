package com.onebyone.kindergarten.domain.address.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SubRegionResponseDTO {
  private Long subRegionId;
  private String name;
  private List<SubRegionResponseDTO> children = new ArrayList<>();
}
