package com.onebyone.kindergarten.domain.feignClient;

import com.onebyone.kindergarten.domain.user.dto.response.KakaoUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "${oauth.url.api}")
public interface KakaoApiClient {
    @GetMapping("/v2/user/me")
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
