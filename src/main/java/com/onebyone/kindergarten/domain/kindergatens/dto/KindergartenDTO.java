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

    private LocalDate openDate;

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

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    public static KindergartenDTO from(com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten kindergarten) {
        KindergartenDTO dto = new KindergartenDTO();
        dto.setName(kindergarten.getName());
        dto.setEstablishment(kindergarten.getEstablishment());
        dto.setEstablishmentDate(kindergarten.getEstablishmentDate());
        dto.setOpenDate(kindergarten.getOpenDate());
        dto.setAddress(kindergarten.getAddress());
        dto.setHomepage(kindergarten.getHomepage());
        dto.setPhoneNumber(kindergarten.getPhoneNumber());
        dto.setClassCount3(kindergarten.getClassCount3());
        dto.setClassCount4(kindergarten.getClassCount4());
        dto.setClassCount5(kindergarten.getClassCount5());
        dto.setPupilCount3(kindergarten.getPupilCount3());
        dto.setPupilCount4(kindergarten.getPupilCount4());
        dto.setPupilCount5(kindergarten.getPupilCount5());
        dto.setMixPupilCount(kindergarten.getMixPupilCount());
        dto.setSpecialPupilCount(kindergarten.getSpecialPupilCount());
        dto.setLatitude(kindergarten.getLatitude());
        dto.setLongitude(kindergarten.getLongitude());
        return dto;
    }
}