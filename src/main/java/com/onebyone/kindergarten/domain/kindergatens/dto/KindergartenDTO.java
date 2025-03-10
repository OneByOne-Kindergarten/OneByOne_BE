package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KindergartenDTO {

    private String name; // 유치원 이름

    private String establishment; // 설립 유형

    private LocalDate establishmentDate; // 설립일

    private String address; // 주소

    private String homepage; // 홈페이지

    private String phoneNumber; // 전화번호

    @JsonProperty("classCount3")
    private Integer classCount3; // 만3세학급수

    @JsonProperty("classCount4")
    private Integer classCount4; // 만4세학급수

    @JsonProperty("classCount5")
    private Integer classCount5; // 만5세학급수

    @JsonProperty("pupilCount3")
    private Integer pupilCount3; // 만3세 유아수

    @JsonProperty("pupilCount4")
    private Integer pupilCount4; // 만4세 유아수

    @JsonProperty("pupilCount5")
    private Integer pupilCount5; // 만5세 유아수

    @JsonProperty("mixPupilCount")
    private Integer mixPupilCount; // 혼합 유아수

    @JsonProperty("specialPupilCount")
    private Integer specialPupilCount; // 특수 유아수

    private Double latitude; // 위도

    private Double longitude; // 경도
}