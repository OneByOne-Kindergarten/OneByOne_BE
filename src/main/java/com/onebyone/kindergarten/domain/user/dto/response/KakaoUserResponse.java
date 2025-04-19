package com.onebyone.kindergarten.domain.user.dto.response;

import com.onebyone.kindergarten.domain.user.dto.kakao.KakaoAccount;
import lombok.Data;

@Data
public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;
}