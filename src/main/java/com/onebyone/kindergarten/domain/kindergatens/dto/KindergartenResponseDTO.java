package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KindergartenResponseDTO {
    private Long id;
    private String name;
    private String establishment;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishmentDate;
    private String address;
    private String homepage;
    private String phoneNumber;
    
    private Integer classCount3;
    private Integer classCount4;
    private Integer classCount5;
    private Integer pupilCount3;
    private Integer pupilCount4;
    private Integer pupilCount5;
    private Integer mixPupilCount;
    private Integer specialPupilCount;
    private Double latitude;
    private Double longitude;
    
    // 추가 계산 필드 추가
    private Integer totalClassCount;
    private Integer totalPupilCount;

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