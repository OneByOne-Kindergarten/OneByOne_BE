package com.onebyone.kindergarten.domain.kindergatens.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Kindergarten extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 유치원 코드 - KINDERCODE

    @Column(nullable = false)
    private String name; // 유치원 이름 - KINDERNAME

    @Column(nullable = false)
    private String establishment; // 설립 유형 - ESTABLISH

    @Column(name = "establishment_date", nullable = false)
    private LocalDate establishmentDate; // 설립일 - EDATE

    @Column(nullable = false)
    private String address; // 주소 - ADDR

    private String homepage; // 홈페이지 - HPADDR

    @Column(name = "phone_number")
    private String phoneNumber; // 전화번호 - TELNO

    // 만3세학급수 - CLCNT3
    @Column(name = "class_count3", nullable = false)
    private Integer classCount3;

    // 만4세학급수 - CLCNT4
    @Column(name = "class_count4",nullable = false)
    private Integer classCount4;

    // 만5세학급수 - CLCNT5
    @Column(name = "class_count5",nullable = false)
    private Integer classCount5;

    // 만3세 유아수 - PPCNT3
    @Column(name = "pupil_count3", nullable = false)
    private Integer pupilCount3;

    // 만4세 유아수 - PPCNT4
    @Column(name = "pupil_count4",nullable = false)
    private Integer pupilCount4;

    // 만5세 유아수 - PPCNT5
    @Column(name = "pupil_count5",nullable = false)
    private Integer pupilCount5;

    // 혼합 유아수 - MIXPPCNT
    @Column(name = "mix_pupil_count", nullable = false)
    private Integer mixPupilCount;

    // 특수 유아수 - SHPPCNT
    @Column(name = "special_pupil_count", nullable = false)
    private Integer specialPupilCount;

    /// TODO : 이후 위도+경도 추가 마이그레이션 작업 필요
    private Double latitude; // 위도
    private Double longitude; // 경도
}