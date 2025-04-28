package com.onebyone.kindergarten.domain.kindergatens.entity;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate; // 개원일 - ODATE

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

    private Double latitude; // 위도
    private Double longitude; // 경도

    @OneToMany(mappedBy = "kindergarten", fetch = FetchType.LAZY)
    private List<KindergartenWorkReview> workReviews;

    @OneToMany(mappedBy = "kindergarten", fetch = FetchType.LAZY)
    private List<KindergartenInternshipReview> internshipReviews;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_internship_review_aggregate_id")
    private KindergartenInternshipReviewAggregate kindergartenInternshipReviewAggregate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_work_review_aggregate_id")
    private KindergartenWorkReviewAggregate kindergartenWorkReviewAggregate;

    /// 유치원 정보 업데이트
    public void update(KindergartenDTO kindergartenDTO) {
        this.name = kindergartenDTO.getName();
        this.establishment = kindergartenDTO.getEstablishment();
        this.establishmentDate = kindergartenDTO.getEstablishmentDate();
        this.openDate = kindergartenDTO.getOpenDate();
        this.address = kindergartenDTO.getAddress();
        this.homepage = kindergartenDTO.getHomepage();
        this.phoneNumber = kindergartenDTO.getPhoneNumber();
        this.classCount3 = kindergartenDTO.getClassCount3();
        this.classCount4 = kindergartenDTO.getClassCount4();
        this.classCount5 = kindergartenDTO.getClassCount5();
        this.pupilCount3 = kindergartenDTO.getPupilCount3();
        this.pupilCount4 = kindergartenDTO.getPupilCount4();
        this.pupilCount5 = kindergartenDTO.getPupilCount5();
        this.mixPupilCount = kindergartenDTO.getMixPupilCount();
        this.specialPupilCount = kindergartenDTO.getSpecialPupilCount();
        this.latitude = kindergartenDTO.getLatitude();
        this.longitude = kindergartenDTO.getLongitude();
    }

    public void updateInternshipReviewAggregate(KindergartenInternshipReviewAggregate kindergartenInternshipReviewAggregate ) {
        this.kindergartenInternshipReviewAggregate = kindergartenInternshipReviewAggregate;
    }

    public void updateWorkReviewAggregate(KindergartenWorkReviewAggregate kindergartenWorkReviewAggregate) {
        this.kindergartenWorkReviewAggregate = kindergartenWorkReviewAggregate;
    }
}