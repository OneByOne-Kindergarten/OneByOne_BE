package com.onebyone.kindergarten.domain.feignClient;

import com.onebyone.kindergarten.domain.user.dto.response.NaverTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverApiClinet", url = "${oauth.naver.url.auth}")
public interface NaverAuthClient {
    @PostMapping(value = "/oauth2.0/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NaverTokenResponse getAccessToken(@RequestParam MultiValueMap<String, String> request);
}
