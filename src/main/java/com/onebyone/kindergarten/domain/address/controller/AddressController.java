package com.onebyone.kindergarten.domain.address.controller;

import com.onebyone.kindergarten.domain.address.dto.AddressResponseDTO;
import com.onebyone.kindergarten.domain.address.service.AddressService;
import com.onebyone.kindergarten.global.facade.AddressFacade;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
  private final AddressFacade addressFacade;
  private final AddressService addressService;

  @PostMapping("/batch/region")
  @Operation(summary = "행정구역 등록 배치", description = "kindergarten 테이블의 region 컬럼에 데이터를 매핑시킵니다")
  public void kindergartenRegionBatch(@AuthenticationPrincipal UserDetails userDetails) {
    addressFacade.regionBatch(Long.valueOf(userDetails.getUsername()));
  }

  @PostMapping("/batch/sub-region")
  @Operation(summary = "시군구 등록 배치", description = "kindergarten 테이블의 subRegion 컬럼에 데이터를 매핑시킵니다")
  public void kindergartenSubRegionBatch(@AuthenticationPrincipal UserDetails userDetails) {
    addressFacade.subRegionBatch(Long.valueOf(userDetails.getUsername()));
  }

  @GetMapping
  @Operation(summary = "주소 조회", description = "주소 정보를 조회합니다.")
  public List<AddressResponseDTO> getAddress() {
    return addressService.getAddress();
  }
}
