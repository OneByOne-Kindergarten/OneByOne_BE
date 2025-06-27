package com.onebyone.kindergarten.domain.user.entity;

import org.springframework.data.redis.core.RedisHash;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "email_certification", timeToLive = 600)
public class EmailCertification {
    @Id
    private String email;

    private String certification;
}
