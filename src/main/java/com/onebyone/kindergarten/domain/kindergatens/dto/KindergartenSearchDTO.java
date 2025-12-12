package com.onebyone.kindergarten.domain.kindergatens.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KindergartenSearchDTO {
  private String name; // 유치원 이름

  private String establishment; // 설립 유형

  private String address; // 주소

  private Integer minClassCount; // 최소 학급 수

  private Integer maxClassCount; // 최대 학급 수
  private Integer minPupilCount; // 최소 원생 수
  private Integer maxPupilCount; // 최대 원생 수
  private Double latitude; // 위도
  private Double longitude; // 경도
  private Double radius; // 반경 (km)
}
