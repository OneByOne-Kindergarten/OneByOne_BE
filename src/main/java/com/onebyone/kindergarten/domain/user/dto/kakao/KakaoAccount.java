package com.onebyone.kindergarten.domain.user.dto.kakao;

import lombok.Data;

@Data
public class KakaoAccount {
    private String email;
    private KakaoProfile profile;
}
