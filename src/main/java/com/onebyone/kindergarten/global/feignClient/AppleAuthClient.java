package com.onebyone.kindergarten.global.feignClient;

import com.onebyone.kindergarten.domain.user.dto.response.ApplePublicKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "appleAuthClient", url = "https://appleid.apple.com")
public interface AppleAuthClient {
  @GetMapping("/auth/keys")
  ApplePublicKeyResponse getPublicKeys();
}
