package com.onebyone.kindergarten.domain.sample.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "sample")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sample extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "phone_number")
    private String reason;
}
