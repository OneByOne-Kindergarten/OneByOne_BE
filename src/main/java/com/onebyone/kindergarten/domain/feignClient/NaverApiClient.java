package com.onebyone.kindergarten.domain.feignClient;

import com.onebyone.kindergarten.domain.user.dto.response.NaverUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NaverApiClient", url = "${oauth.naver.url.api}")
public interface NaverApiClient {
    @GetMapping("/v1/nid/me")
    NaverUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
