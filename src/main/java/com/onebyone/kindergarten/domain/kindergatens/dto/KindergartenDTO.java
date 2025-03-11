package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KindergartenDTO {
    private String name;

    private String establishment;

    private LocalDate establishmentDate;

    private String address;

    private String homepage;

    private String phoneNumber;

    @JsonProperty("classCount3")
    private Integer classCount3;

    @JsonProperty("classCount4")
    private Integer classCount4;

    @JsonProperty("classCount5")
    private Integer classCount5;
    
    @JsonProperty("pupilCount3")
    private Integer pupilCount3;

    @JsonProperty("pupilCount4")
    private Integer pupilCount4;

    @JsonProperty("pupilCount5")
    private Integer pupilCount5;

    @JsonProperty("mixPupilCount")
    private Integer mixPupilCount;

    @JsonProperty("specialPupilCount")
    private Integer specialPupilCount;
    
    private Double latitude;

    private Double longitude;
}