package com.onebyone.kindergarten.global.batch.processor;

import com.onebyone.kindergarten.domain.address.entity.SubRegion;
import com.onebyone.kindergarten.domain.address.repository.RegionRepository;
import com.onebyone.kindergarten.domain.address.repository.SubRegionRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class KindergartenSubRegionProcessor implements ItemProcessor<Kindergarten, Kindergarten> {
  private final RegionRepository regionRepository;
  private final SubRegionRepository subRegionRepository;

  // Batch 시작 시 캐시
  private final Map<String, Long> cityMap;
  private final Map<Long, Map<String, SubRegion>> subRegionMap;

  @Override
  public Kindergarten process(Kindergarten item) {
    String[] splitAddress = item.getAddress().split(" ");

    if (splitAddress.length < 3) {
      System.out.println("주소 형식 이상: " + item.getId() + " / " + item.getAddress());
      return item;
    }

    String city = splitAddress[0];
    String region = splitAddress[1];
    String subRegion = splitAddress[2];

    Long cityId = cityMap.get(city);

    if (cityId == null) {
      if ("전북특별자치도".equals(city)) {
        cityId = cityMap.get("전라북도");
      } else if ("경기".equals(city)) {
        cityId = cityMap.get("경기도");
      } else {
        System.out.println(
            "등록되지 않은 city: " + item.getId() + " / " + city + " / " + item.getAddress());
        return item;
      }
    }

    Map<String, SubRegion> subMap = subRegionMap.get(cityId);

    if (subMap == null) {
      System.out.println(
          "서브리전 없음: " + item.getId() + " / cityId=" + cityId + " / " + item.getAddress());
      return item;
    }

    char lastChar = subRegion.charAt(subRegion.length() - 1);
    SubRegion subRegionEntity;

    if ('시' == lastChar || '군' == lastChar || '구' == lastChar) {
      subRegionEntity = subMap.get(subRegion);
    } else {
      subRegionEntity = subMap.get(region);
    }

    if (subRegionEntity == null) {
      System.out.println(
          "해당 subRegion 없음: " + item.getId() + " / " + subRegion + " / " + item.getAddress());
      return item;
    }

    item.updateSubRegion(subRegionEntity.getSubRegionId());
    return item;
  }

  public static KindergartenSubRegionProcessor create(
      RegionRepository regionRepository, SubRegionRepository subRegionRepository) {
    Map<String, Long> cityMap =
        regionRepository.findAll().stream()
            .collect(Collectors.toMap(r -> r.getName(), r -> r.getRegionId()));

    Map<Long, Map<String, SubRegion>> subRegionMap =
        subRegionRepository.findAll().stream()
            .collect(
                Collectors.groupingBy(
                    SubRegion::getRegionId, Collectors.toMap(SubRegion::getName, s -> s)));

    return new KindergartenSubRegionProcessor(
        regionRepository, subRegionRepository, cityMap, subRegionMap);
  }
}
