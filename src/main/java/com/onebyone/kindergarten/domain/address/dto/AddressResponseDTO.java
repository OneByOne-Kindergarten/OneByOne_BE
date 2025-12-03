package com.onebyone.kindergarten.domain.address.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddressResponseDTO {
    private Long regionId;
    private String regionName;
    private List<SubRegionResponseDTO> subRegions;
}