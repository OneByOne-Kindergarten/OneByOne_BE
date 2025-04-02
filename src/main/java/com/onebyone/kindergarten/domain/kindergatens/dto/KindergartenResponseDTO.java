package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KindergartenResponseDTO {
    private final Long id;
    private final String name;
    private final String establishment;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate establishmentDate;
    private final String address;
    private final String homepage;
    private final String phoneNumber;
    
    private final Integer classCount3;
    private final Integer classCount4;
    private final Integer classCount5;
    private final Integer pupilCount3;
    private final Integer pupilCount4;
    private final Integer pupilCount5;
    private final Integer mixPupilCount;
    private final Integer specialPupilCount;
    private final Double latitude;
    private final Double longitude;
    private final Integer totalClassCount;
    private final Integer totalPupilCount;

    // JPQL 생성자
    public KindergartenResponseDTO(
            Long id, String name, String establishment, LocalDate establishmentDate,
            String address, String homepage, String phoneNumber,
            Integer classCount3, Integer classCount4, Integer classCount5,
            Integer pupilCount3, Integer pupilCount4, Integer pupilCount5,
            Integer mixPupilCount, Integer specialPupilCount,
            Double latitude, Double longitude) {
        
        this.id = id;
        this.name = name;
        this.establishment = establishment;
        this.establishmentDate = establishmentDate;
        this.address = address;
        this.homepage = homepage;
        this.phoneNumber = phoneNumber;
        this.classCount3 = classCount3;
        this.classCount4 = classCount4;
        this.classCount5 = classCount5;
        this.pupilCount3 = pupilCount3;
        this.pupilCount4 = pupilCount4;
        this.pupilCount5 = pupilCount5;
        this.mixPupilCount = mixPupilCount;
        this.specialPupilCount = specialPupilCount;
        this.latitude = latitude;
        this.longitude = longitude;
        
        // 총계 계산
        this.totalClassCount = classCount3 + classCount4 + classCount5;
        this.totalPupilCount = pupilCount3 + pupilCount4 + pupilCount5 + mixPupilCount + specialPupilCount;
    }

    // 모든 필드를 포함하는 생성자 (Builder용)
    @Builder
    public KindergartenResponseDTO(
            Long id, String name, String establishment, LocalDate establishmentDate,
            String address, String homepage, String phoneNumber,
            Integer classCount3, Integer classCount4, Integer classCount5,
            Integer pupilCount3, Integer pupilCount4, Integer pupilCount5,
            Integer mixPupilCount, Integer specialPupilCount,
            Double latitude, Double longitude,
            Integer totalClassCount, Integer totalPupilCount) {
        
        this.id = id;
        this.name = name;
        this.establishment = establishment;
        this.establishmentDate = establishmentDate;
        this.address = address;
        this.homepage = homepage;
        this.phoneNumber = phoneNumber;
        this.classCount3 = classCount3;
        this.classCount4 = classCount4;
        this.classCount5 = classCount5;
        this.pupilCount3 = pupilCount3;
        this.pupilCount4 = pupilCount4;
        this.pupilCount5 = pupilCount5;
        this.mixPupilCount = mixPupilCount;
        this.specialPupilCount = specialPupilCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalClassCount = totalClassCount;
        this.totalPupilCount = totalPupilCount;
    }

    public static KindergartenResponseDTO from(Kindergarten kindergarten) {
        int totalClass = kindergarten.getClassCount3() + kindergarten.getClassCount4() + kindergarten.getClassCount5();
        int totalPupil = kindergarten.getPupilCount3() + kindergarten.getPupilCount4() + kindergarten.getPupilCount5() 
                        + kindergarten.getMixPupilCount() + kindergarten.getSpecialPupilCount();

        return KindergartenResponseDTO.builder()
                .id(kindergarten.getId())
                .name(kindergarten.getName())
                .establishment(kindergarten.getEstablishment())
                .establishmentDate(kindergarten.getEstablishmentDate())
                .address(kindergarten.getAddress())
                .homepage(kindergarten.getHomepage())
                .phoneNumber(kindergarten.getPhoneNumber())
                .classCount3(kindergarten.getClassCount3())
                .classCount4(kindergarten.getClassCount4())
                .classCount5(kindergarten.getClassCount5())
                .pupilCount3(kindergarten.getPupilCount3())
                .pupilCount4(kindergarten.getPupilCount4())
                .pupilCount5(kindergarten.getPupilCount5())
                .mixPupilCount(kindergarten.getMixPupilCount())
                .specialPupilCount(kindergarten.getSpecialPupilCount())
                .latitude(kindergarten.getLatitude())
                .longitude(kindergarten.getLongitude())
                .totalClassCount(totalClass)
                .totalPupilCount(totalPupil)
                .build();
    }
} 