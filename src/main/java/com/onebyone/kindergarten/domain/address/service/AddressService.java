package com.onebyone.kindergarten.domain.address.service;

import com.onebyone.kindergarten.domain.address.dto.*;
import com.onebyone.kindergarten.domain.address.entity.Region;
import com.onebyone.kindergarten.domain.address.entity.SubRegion;
import com.onebyone.kindergarten.domain.address.repository.RegionRepository;
import com.onebyone.kindergarten.domain.address.repository.SubRegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final RegionRepository regionRepository;
    private final SubRegionRepository subRegionRepository;

    public List<AddressResponseDTO> getAddress() {
        List<RegionDTO> regions = regionRepository.findAll().stream().map(Region::toDto).toList();
        List<SubRegionDTO> subRegions = subRegionRepository.findAll().stream().map(SubRegion::toDTO).toList();

        // 시군구 map 생성
        Map<Long, SubRegionResponseDTO> subRegionMap = subRegions.stream()
                .collect(Collectors.toMap(
                        SubRegionDTO::getSubRegionId,
                        sub -> {
                            SubRegionResponseDTO dto = new SubRegionResponseDTO();
                            dto.setSubRegionId(sub.getSubRegionId());
                            dto.setName(sub.getName());
                            return dto;
                        }
                ));

        // 하위 SubRegion 연결
        List<SubRegionResponseDTO> rootSubRegions = new ArrayList<>();
        for (SubRegionDTO sub : subRegions) {
            SubRegionResponseDTO dto = subRegionMap.get(sub.getSubRegionId());
            if (sub.getParentId() == null) {
                rootSubRegions.add(dto);
            } else {
                SubRegionResponseDTO parent = subRegionMap.get(sub.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        // Region -> SubRegion 매핑
        return regions.stream()
                .map(r -> {
                    AddressResponseDTO dto = new AddressResponseDTO();
                    dto.setRegionId(r.getId());
                    dto.setRegionName(r.getName());
                    List<SubRegionResponseDTO> regionSubRegions = rootSubRegions.stream()
                            .filter(s -> subRegions.stream()
                                    .anyMatch(sr -> sr.getSubRegionId().equals(s.getSubRegionId())
                                            && sr.getRegionId().equals(r.getId())))
                            .toList();
                    dto.setSubRegions(regionSubRegions);
                    return dto;
                })
                .toList();
    }
}
