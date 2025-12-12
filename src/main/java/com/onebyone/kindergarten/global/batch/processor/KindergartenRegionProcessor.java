package com.onebyone.kindergarten.global.batch.processor;

import com.onebyone.kindergarten.domain.address.entity.Region;
import com.onebyone.kindergarten.domain.address.repository.RegionRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class KindergartenRegionProcessor implements ItemProcessor<Kindergarten, Kindergarten> {
  private final RegionRepository regionRepository;

  // Batch 시작 시 캐시
  private final Map<String, Region> cityMap;

  @Override
  public Kindergarten process(Kindergarten item) {
    String[] splitAddress = item.getAddress().split(" ");

    if (splitAddress.length < 3) {
      System.out.println("주소 형식 이상: " + item.getId() + " / " + item.getAddress());
      return item;
    }

    String city = splitAddress[0];

    if ("전북특별자치도".equals(city)) {
      city = "전라북도";
    } else if ("경기".equals(city)) {
      city = "경기도";
    }

    Region region = cityMap.get(city);

    item.updateRegion(region.getRegionId());
    return item;
  }

  public static KindergartenRegionProcessor create(RegionRepository regionRepository) {
    Map<String, Region> cityMap =
        regionRepository.findAll().stream().collect(Collectors.toMap(Region::getName, r -> r));

    return new KindergartenRegionProcessor(regionRepository, cityMap);
  }
}
