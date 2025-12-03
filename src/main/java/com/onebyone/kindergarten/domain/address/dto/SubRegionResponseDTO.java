package com.onebyone.kindergarten.domain.address.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubRegionResponseDTO {
    private Long subRegionId;
    private String name;
    private List<SubRegionResponseDTO> children = new ArrayList<>();
}
