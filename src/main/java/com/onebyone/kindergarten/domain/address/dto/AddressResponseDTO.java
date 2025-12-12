package com.onebyone.kindergarten.domain.address.dto;

import java.util.List;
import lombok.Data;

@Data
public class AddressResponseDTO {
  private Long regionId;
  private String regionName;
  private List<SubRegionResponseDTO> subRegions;
}
